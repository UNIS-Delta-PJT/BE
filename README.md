# 산출물

```markdown
## 개발 환경

| 분류 | 상세 정보 |
| --- | --- |
| **Java Version** | Java 17 |
| **Framework** | Spring Boot 4.0.6 |
| **Build Tool** | Gradle |
| **Database** | MySQL 8.0 (Local / Deploy) |
| **OS/Environment** | Windows 11 / Amazon Linux 2023 (Ubuntu) |

<br>

## 기술 스택

### Backend Framework & Core
* **Spring Boot**: 애플리케이션 전반의 아키텍처 및 코어 비즈니스 로직 제어
* **Spring Security**: 보안, CORS 필터 구성, 인증/인가 프로세스 관리

### Data Access & Persistence
* **Spring Data JPA**: 가계부 및 사용자 도메인의 객체 지향 데이터 모델링 및 쿼리 추상화
* **Hibernate 7.2.n**: JPA 인터페이스 규격의 ORM 구현체 및 복잡한 통계·집계용 JPQL 쿼리 처리

### Authentication & Security
* **Kakao Login API (OAuth 2.0)**: 프론트엔드와 유기적으로 연동하여 카카오 고유 식별 번호 기반 소셜 로그인 처리
* **jjwt (io.jsonwebtoken) 0.12.6**: 데이터 암호화 및 무상태(Stateless) 아키텍처 구현을 위한 고유 JWT(Access/Refresh) 토큰 발급 및 검증
* **RTR (Refresh Token Rotation)**: Refresh Token의 탈취 위험을 방지하기 위한 토큰 생명주기 및 DB 교체 로직 설계

### Infrastructure & Deployment
* **AWS EC2 (Ubuntu)**: 백엔드 애플리케이션 구동을 위한 클라우드 가상 서버 인프라
* **GitHub Actions**: 코드 형상 관리 시스템 연동 기반 빌드 및 무중단 배포(CI/CD) 자동화 파이프라인 구축
* **Cloudflare Tunnel**: 도메인 구매 없이 외부 트래픽을 안전하게 중계하고 인프라 비용을 최소화하기 위한 무료 HTTPS 환경 구성
```

---

## ERD

![DELTA ERD](https://app.notion.com./UNIS_Delta_Project_ERD.png)

- [ERD 상세 설명](https://app.notion.com/p/ERD.md)

---

## API 명세서

- [API 명세서 확인](https://app.notion.com/p/API.md)
