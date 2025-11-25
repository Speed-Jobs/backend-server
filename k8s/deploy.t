apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${SERVICE_NAME}
  namespace: ${NAMESPACE}
spec:
  replicas: ${REPLICAS}
  selector:
    matchLabels:
      app: ${SERVICE_NAME}
  template:
    metadata:
      annotations:
        prometheus.io/scrape: 'true'
        prometheus.io/port: '8080'
        prometheus.io/path: '/actuator/prometheus'
        update: ${HASHCODE}
      labels:
        app: ${SERVICE_NAME}
    spec:
      serviceAccountName: default
      containers:
      - name: ${IMAGE_NAME}
        image: ${DOCKER_REGISTRY}/${IMAGE_NAME}:${VERSION}
        imagePullPolicy: Always
        ports:
        - name: http
          containerPort: 8080
          protocol: TCP
        - name: mgmt
          containerPort: 8081
          protocol: TCP
        env:
        - name: NAMESPACE
          value: ${NAMESPACE}
        - name: SPRING_PROFILES_ACTIVE  
          value: "prod"
        - name: DB_HOST
          value: "speedjobs-mysql.skala-practice.svc.cluster.local"
        - name: DB_PORT
          value: "3306"
        - name: DB_USER
          valueFrom:
            secretKeyRef:
              name: speedjobs-mysql-secret
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: speedjobs-mysql-secret
              key: password
        - name: DB_NAME
          value: "speedjobs"