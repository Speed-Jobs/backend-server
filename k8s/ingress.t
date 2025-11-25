apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
  name: ${SERVICE_NAME}
  namespace: ${NAMESPACE}
spec:
  ingressClassName: public-nginx
  rules:
  - host: ${SERVICE_NAME}-ingress.skala25a.project.skala-ai.com
    http:
      paths:
      - backend:
          service:
            name: ${SERVICE_NAME}
            port:
              number: 8080
        path: /
        pathType: Prefix
  tls:
  - hosts:
    - '${SERVICE_NAME}-ingress.skala25a.project.skala-ai.com'
    secretName: ${SERVICE_NAME}-ingress-project-tls-cert
