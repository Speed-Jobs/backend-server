i#!/bin/bash

# 사용 방법을 출력하는 함수
usage() {
    echo "Usage: $0 [-b|--build] [-p|--push] [-y|--yaml] [-d|--deploy] [-r|--remove] [-a|--all] [-f|--file <env-file>]"
    echo "  -c, --compile  compile exec file"
    echo "  -b, --build    Build docker"
    echo "  -p, --push     Build docker image and push docker image to registry"
    echo "  -y, --yaml     Convert .t files to .yaml"
    echo "  -d, --deploy   Deploy docker to k8s cluster using generated .yaml files"
    echo "  -r, --remove   Remove all deployed resources from k8s cluster"
    echo "  -a, --all      Run all steps in order"
    echo "  -f, --file     Specify env properties file"
    exit 1
}

# 옵션 없이 스크립트를 실행한 경우 사용 방법 출력
if [ $# -eq 0 ]; then
    usage
fi

# 옵션 처리를 위한 변수
run_compile=0
run_build=0
run_push=0
run_yaml=0
run_deploy=0
run_remove=0
import_file=0
run_all=0
env_file="env.properties"

# 스크립트의 디렉토리 경로를 얻음
script_dir="$(dirname "$0")"

# 옵션 파싱
while [[ "$#" -gt 0 ]]; do
    case $1 in
        -c|--compile) run_compile=1 ;;
        -b|--build) run_build=1 ;;
        -p|--push) run_push=1 ;;
        -y|--yaml) run_yaml=1 ;;
        -d|--deploy) run_deploy=1 ;;
        -r|--remove) run_remove=1 ;;
        -a|--all) run_all=1 ;;
        -f|--file)
            import_file=1
            shift
            env_file=$1
            ;;
        *) usage ;;
    esac
    shift
done


# 환경 변수 로드
if [[ -f $env_file ]]; then
    while IFS= read -r line; do
        # 주석이나 빈 줄 무시
        if [[ $line =~ ^\s*# ]] || [[ -z $line ]]; then
            continue
        fi

        # '=' 기호를 기준으로 key와 value 분리
        key=$(echo "$line" | cut -d '=' -f 1)
        value=$(echo "$line" | cut -d '=' -f 2-)

        # 앞뒤 공백 제거
        key=$(echo "$key" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')
        value=$(echo "$value" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//')

        # 따옴표 제거 (있는 경우)
        value=$(echo "$value" | sed -e 's/^"//' -e 's/"$//')
        value=$(echo "$value" | sed -e "s/^'//" -e "s/'$//")

        # 변수 설정
        declare "$key=$value"
    done < "$env_file"

    # DEPLOY_FILE_LIST 처리 (쉼표 제거)
    if [[ -n $DEPLOY_FILE_LIST ]]; then
        DEPLOY_FILE_LIST=$(echo "$DEPLOY_FILE_LIST" | tr ',' ' ')
        #echo "DEPLOY_FILE_LIST: $DEPLOY_FILE_LIST"
    fi
else
    echo "$env_file file not found."
    exit 1
fi

# 스크립트 실행

if [[ $run_compile -eq 1 || $run_all -eq 1 ]]; then
    if [[ -n $JAR_FILE_PATH ]]; then
        ${script_dir}/build.sh
    fi
fi

if [[ $run_build -eq 1 || $run_all -eq 1 ]]; then
    "${script_dir}/build-docker.sh" -f "$env_file"
fi

if [[ $run_push -eq 1 || $run_all -eq 1 ]]; then
    "${script_dir}/build-push.sh" -f "$env_file"
fi


# DEPLOY_FILE_LIST에서 파일 이름 추출 및 처리
read -ra deploy_files <<< "$DEPLOY_FILE_LIST"
processed_files=()

for file in "${deploy_files[@]}"; do
    # .yaml 확장자 제거
    file_name=$(echo "$file" | sed -e 's/\.yaml$//')
    if [[ -n "$file_name" ]]; then
        processed_files+=("$file_name")
    fi
done

# DEPLOY_FILE_NAME에서 확장자 제거
deploy_file_name=$(echo "$DEPLOY_FILE_NAME" | sed 's/\.[^.]*$//')

# DEPLOY_FILE_NAME을 processed_files 배열의 맨 앞에 추가 (중복 제거)
#processed_files=($(echo "${deploy_file_name} ${processed_files[@]}" | tr ' ' '\n' | awk '!seen[$0]++' | tr '\n' ' '))
processed_files=($(echo "${processed_files[@]}" | tr ' ' '\n' | awk '!seen[$0]++' | tr '\n' ' '))

#echo "Processed files: ${processed_files[@]}"



if [[ $run_yaml -eq 1 || $run_all -eq 1 ]]; then
    # 해시코드 생성
    CURRENT_TIMESTAMP=$(date +%Y%m%d%H%M%S)
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # MacOS에서 해시 코드 생성
        HASHCODE=$(echo -n "$CURRENT_TIMESTAMP$(cat "$env_file")" | md5 | cut -d' ' -f4)
    else
        # 다른 운영 체제에서 해시 코드 생성
        HASHCODE=$(echo -n "$CURRENT_TIMESTAMP$(cat "$env_file")" | md5sum | cut -d' ' -f1)
    fi

    # processed_files 배열에 있는 각 파일을 처리
    for file_name in "${processed_files[@]}"; do
        file="${DEPLOY_PATH}/${file_name}.t"
        new_file="${DEPLOY_PATH}/${file_name}.yaml"
        #echo "Processing $file..."
        echo " "
        if [[ -f "$file" ]]; then
            sed -e "s#\${DOCKER_REGISTRY}#${DOCKER_REGISTRY}#g" \
                -e "s#\${IMAGE_NAME}#${IMAGE_NAME}#g" \
                -e "s#\${VERSION}#${VERSION}#g" \
                -e "s#\${HASHCODE}#${HASHCODE}#g" \
                -e "s#\${NAMESPACE}#${NAMESPACE}#g" \
                -e "s#\${LOGGING_LEVEL}#${LOGGING_LEVEL}#g" \
                -e "s#\${DOMAIN_URL}#${DOMAIN_URL}#g" \
                -e "s#\${KEYCLOAK_URL}#${KEYCLOAK_URL}#g" \
                -e "s#\${IDE_PROXY_DOMAIN}#${IDE_PROXY_DOMAIN}#g" \
                -e "s#\${KEYCLOAK_URL}#${KEYCLOAK_URL}#g" \
                -e "s#\${KEYCLOAK_CLIENT_ID}#${KEYCLOAK_CLIENT_ID}#g" \
                -e "s#\${KEYCLOAK_CLIENT_SECRET}#${KEYCLOAK_CLIENT_SECRET}#g" \
                -e "s#\${IDE_PROXY_HOST_PATTERN}#${IDE_PROXY_HOST_PATTERN}#g" \
                -e "s#\${IDE_PROXY_SUBDOMAIN_PATTERN}#${IDE_PROXY_SUBDOMAIN_PATTERN}#g" \
                -e "s#\${VSCODE_SERVER_VERSION}#${VSCODE_SERVER_VERSION}#g" \
                -e "s#\${SSH_SERVER_VERSION}#${SSH_SERVER_VERSION}#g" \
                -e "s#\${USER_BLOCK_STORAGE_NAME}#${USER_BLOCK_STORAGE_NAME}#g" \
                -e "s#\${USER_FILE_STORAGE_NAME}#${USER_FILE_STORAGE_NAME}#g" \
                -e "s#\${MINIO_ALIAS}#${MINIO_ALIAS}#g" \
                -e "s#\${MINIO_URL}#${MINIO_URL}#g" \
                -e "s#\${MINIO_ACCESS_KEY}#${MINIO_ACCESS_KEY}#g" \
                -e "s#\${MINIO_SECRET_KEY}#${MINIO_SECRET_KEY}#g" \
                -e "s#\${RDE_OPERATOR_URI}#${RDE_OPERATOR_URI}#g" \
                -e "s#\${SERVICE_NAME}#${SERVICE_NAME}#g" \
                -e "s#\${SERVICE_URL}#${SERVICE_URL}#g" \
                -e "s#\${CLUSTER_ROLE_ADMIN}#${CLUSTER_ROLE_ADMIN}#g" \
                -e "s#\${CLUSTER_ROLE_VIEW}#${CLUSTER_ROLE_VIEW}#g" \
                -e "s#\${IMAGE_PULL_SECRET}#${IMAGE_PULL_SECRET}#g" \
                -e "s#\${TAINT_KEY}#${TAINT_KEY}#g" \
                -e "s#\${TAINT_VALUE}#${TAINT_VALUE}#g" \
                -e "s#\${TAINT_EFFECT}#${TAINT_EFFECT}#g" \
                -e "s#\${USER_NAME}#${USER_NAME}#g" \
                -e "s#\${CONTAINER_PORT}#${CONTAINER_PORT}#g" \
                -e "s#\${SERVICE_NAME}#${SERVICE_NAME}#g" \
                -e "s#\${REPLICAS}#${REPLICAS}#g" \
                -e "s#\${PROFILE}#${PROFILE}#g" \
                "$file" > "$new_file"
            #echo "Generated $new_file."
            echo "---"
            cat $new_file
        else
            echo "Template file $file not found."
        fi
    done
fi

if [[ $run_remove -eq 1 ]]; then
    echo "Removing deployed resources..."
    # processed_files 배열에 있는 각 파일을 처리
    for file_name in "${processed_files[@]}"; do
        yaml_file="${DEPLOY_PATH}/${file_name}.yaml"
        if [[ -f "$yaml_file" ]]; then
            echo "Removing resources from $yaml_file..."
            kubectl delete -f "$yaml_file"
        else
            echo "YAML file $yaml_file not found."
        fi
    done
fi

if [[ $run_deploy -eq 1 || $run_all -eq 1 ]]; then
    # processed_files 배열에 있는 각 파일을 처리
    for file_name in "${processed_files[@]}"; do
        new_file="${DEPLOY_PATH}/${file_name}.yaml"
        if [[ -f "$new_file" ]]; then
            kubectl apply -f "$new_file"
        else
            echo "YAML file $new_file not found."
        fi
    done
fi