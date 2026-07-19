# API 명세서

사람: 이현경
상위 항목: 데모데이 (https://app.notion.com/p/38f359f529c18035befef467a2c2d5b0?pvs=21)
상태: 시작 전

- 프롬프트
    
    ```markdown
    # DELTA API 명세서
    
    ---
    
    # 공통 응답 형식
    
    ### 성공 응답
    
    ```json
    {
      "success": true,
      "data": {},
      "message": "요청이 성공적으로 처리되었습니다."
    }
    ```
    
    ### 실패 응답
    
    ```json
    {
      "timestamp": "2026-07-14T00:15:30.347+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_REQUEST",
      "message": "잘못된 요청입니다.",
      "path": "/api/v1/example"
    }
    ```
    
    ---
    
    # 1. Auth
    
    ---
    
    # 카카오 로그인
    
    Method: POST
    URI patterns: /api/v1/auth/kakao/login
    완료: Yes
    
    # 설명 및 권한
    
    카카오 인가 코드를 이용해 사용자를 인증한다. 신규 사용자라면 User를 생성하고, 기존 사용자라면 저장된 사용자 정보를 조회한다. 인증이 완료되면 DELTA Access Token과 Refresh Token을 발급한다.
    
    로그인하지 않은 사용자도 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/auth/kakao/login`
    
    ex) `/api/v1/auth/kakao/login`
    
    # 필요 정보
    
    ## Request Body
    
    ```json
    {
      "kakaoAccessToken": "kakao_authorization_code"
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | authorizationCode | string | O | 카카오 로그인 후 발급받은 일회성 인가 코드 |
    
    `redirectUri`는 클라이언트가 전달하지 않는다.
    
    서버의 환경변수 또는 설정 파일에 등록된 카카오 Redirect URI를 사용한다.
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK` (기존 유저 로그인)
    
    ### Header
    
    ```json
    Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiJ9...; HttpOnly; Secure; SameSite=None; Path=/
    ```
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "isNewUser": false, 
        "userId": 1
      },
      "message": "카카오 로그인 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | accessToken | string | DELTA API 요청에 사용하는 Access Token |
    | tokenType | string | 토큰 인증 방식. `Bearer`를 반환 |
    | expiresIn | number | Access Token 만료까지 남은 시간(초) |
    | isNewUser | boolean | 이번 로그인 과정에서 신규 가입되었는지 여부 |
    | userId | number | DELTA 내부 사용자 ID |
    
    ## HTTP Status code: `201 Created` (신규 회원가입)
    
    ### Header
    
    ```json
    Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiJ9...; HttpOnly; Secure; SameSite=None; Path=/
    ```
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 3600,
        "isNewUser": true, 
        "userId": 1,
      },
      "message": "카카오 로그인 성공"
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-14T00:15:30.347+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "KAKAO_AUTHENTICATION_FAILED",
      "message": "카카오 인증에 실패했습니다.",
      "path": "/api/auth/kakao/login"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | KAKAO_AUTHENTICATION_FAILED | 카카오 인증에 실패한 경우 |
    
    ---
    
    # 토큰 재발급
    
    Method: POST
    URI patterns: /api/v1/auth/reissue
    완료: Yes
    
    # 설명 및 권한
    
    Refresh Token을 검증하고 새로운 Access Token을 발급한다. 필요하면 Refresh Token도 함께 교체한다.
    
    유효한 Refresh Token을 가진 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/auth/reissue`
    
    ex) `/api/v1/auth/reissue`
    
    # 필요 정보
    
    ## Cookie
    
    ```
    Cookie: refreshToken=eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | refreshToken | string | O | HttpOnly Cookie에 저장된 DELTA Refresh Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Header
    
    Refresh Token이 교체된 경우 다음과 같이 새 쿠키를 반환한다.
    
    ```
    Set-Cookie: refreshToken=new_refresh_token; HttpOnly; Secure; SameSite=None; Path=/
    ```
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    	  "tokenType": "Bearer",
    	  "expiresIn": 3600,
    	  "refreshTokenRotated": true
      },
      "message": "토큰 재발급 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | accessToken | string | 새로 발급된 Access Token |
    | tokenType | string | 토큰 인증 방식. `Bearer`를 반환 |
    | expiresIn | number | Access Token 만료까지 남은 시간(초) |
    | refreshTokenRotated | boolean | Refresh Token이 새 토큰으로 교체되었는지 여부 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized` (토큰이 유효하지 않은 경우)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-14T00:15:30.347+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_REFRESH_TOKEN",
      "message": "유효하지 않은 Refresh Token입니다.",
      "path": "/api/v1/auth/reissue"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized` (토큰이 만료된 경우)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-14T00:15:30.347+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "EXPIRED_REFRESH_TOKEN",
      "message": "Refresh Token이 만료되었습니다. 다시 로그인해 주세요.",
      "path": "/api/v1/auth/reissue"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-14T00:15:30.347+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_NOT_FOUND",
      "message": "토큰에 연결된 사용자가 존재하지 않습니다.",
      "path": "/api/v1/auth/reissue"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_REFRESH_TOKEN | 토큰 서명이 올바르지 않거나 형식이 잘못된 경우 |
    | 404 Not Found | USER_NOT_FOUND | 토큰에 연결된 사용자가 존재하지 않는 경우 |
    | 401 Unauthorized | EXPIRED_REFRESH_TOKEN | Refresh Token의 유효기간이 지난 경우 |
    
    ---
    
    # 로그아웃
    
    Method: POST
    URI patterns: /api/v1/auth/logout
    완료: Yes
    
    # 설명 및 권한
    
    현재 사용자의 Refresh Token을 폐기하고 Refresh Token 쿠키를 삭제한다. Access Token은 서버에 별도로 저장하지 않는 경우 즉시 폐기되지 않고, 남은 유효기간 동안 형식상 유효할 수 있다. 따라서 Access Token의 유효기간은 짧게 설정한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/auth/logout`
    
    ex) `/api/v1/auth/logout`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    ## Cookie
    
    ```
    Cookie: refreshToken=eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식 |
    | Cookie | refreshToken | string | O | 폐기할 DELTA Refresh Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Header
    
    ```
    Set-Cookie: refreshToken=; Max-Age=0; HttpOnly; Secure; SameSite=None; Path=/
    ```
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": null,
      "message": "성공적으로 로그아웃 되었습니다."
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized` (로그인이 되어 있지 않은 경우)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-14T00:15:30.347+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "UNAUTHORIZED",
      "message": "로그인이 필요합니다.",
      "path": "/api/v1/auth/logout"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized` (토큰이 유효하지 않은 경우)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-14T00:15:30.347+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_REFRESH_TOKEN",
      "message": "유효하지 않은 Refresh Token입니다.",
      "path": "/api/v1/auth/logout"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | UNAUTHORIZED | Access Token이 없거나 유효하지 않은 경우 |
    | 401 Unauthorized | INVALID_REFRESH_TOKEN | Refresh Token 검증에 실패한 경우 |
    
    ---
    
    # 2. User
    
    ---
    
    # 캐릭터 설정
    
    Method: PATCH
    URI patterns: /api/v1/users/character
    완료: Yes
    
    # 설명 및 권한
    
    캐릭터의 닉네임, 몸통 색상, 눈 모양을 설정할 수 있다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `PATCH /api/v1/users/character`
    
    ex) `/api/v1/users/character`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    없음
    
    ## Request Body
    
    ```json
    {
      "nickname": "델타초보",
      "bodyColor" : "WHITE",
      "eyeShape" : "DEFAULT"
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | nickname | string | X | 캐릭터 닉네임 |
    | bodyColor | string | X | 몸통 색상(WHITE, PINK, PURPLE, SKYBLUE, YELLOW, GREEN, ORANGE, MINT 중 하나) |
    | eyeShape | string | X | 눈 모양(DEFAULT, HAPPY, WINK, SMILE, DEAD, HEART 중 하나) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": null,
      "message": "캐릭터 설정 성공"
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "지원하지 않는 몸통 색상 혹은 눈 모양입니다.",
      "path": "/api/v1/users/character"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/users/character"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_NOT_FOUND",
      "message": "사용자를 찾을 수 없습니다.",
      "path": "/api/v1/users/character"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | 지정된 Enum 값 외의 문자열이 들어오거나 닉네임 형식이 틀린 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | USER_NOT_FOUND | 토큰에 연결된 사용자 ID가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 내 정보 조회
    
    Method: GET
    URI patterns: /api/v1/users/me
    완료: Yes
    
    # 설명 및 권한
    
    사용자의 모든 정보를 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/users/me`
    
    ex) `/api/v1/users/me`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    없음
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "userId": 1,
        "coinBalance": 1500,
        "continuousAttendance": 31,
        "mapPosition": 5,
        "character": {
          "nickname": "델타초보",
          "bodyColor": "PINK",
          "eyeShape": "HAPPY"
        },
        "notification": {
          "isPushEnabled": true,
          "isNightPushDisabled": false,
          "fcmToken": "token_string"
        },
        "equippedItems": [
          { "itemId": 101, "itemType": "TOP" },
          { "itemId": 205, "itemType": "HAT" }
        ]
      },
      "message": "내 정보 조회 성공"
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/users/me"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_NOT_FOUND",
      "message": "사용자를 찾을 수 없습니다.",
      "path": "/api/v1/users/me"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | USER_NOT_FOUND | 토큰에 연결된 사용자 ID가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 알림 설정 변경
    
    Method: PATCH
    URI patterns: /api/v1/users/notifications
    완료: Yes
    
    # 설명 및 권한
    
    사용자가 전체 푸시 알림 수신 여부 또는 야간 알림 방해금지(22:00-06:00) 설정을 변경한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `PATCH /api/v1/users/notifications`
    
    ex) `/api/v1/users/notifications`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    없음
    
    ## Request Body
    
    ```json
    {
      "isPushEnabled": true,
      "isNightPushDisabled": true
    }
    ```
    
    | **name** | **type** | **필수** | **설명** |
    | --- | --- | --- | --- |
    | isPushEnabled | boolean | O | 전체 푸시 알림 동의 여부 (true: 켜짐, false: 꺼짐) |
    | isNightPushDisabled | boolean | O | 야간 알림 방해금지 여부 (true: 밤에 안 받음, false: 밤에도 받음) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": null,
      "message": "알림 설정이 성공적으로 변경되었습니다."
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "잘못된 입력값입니다. boolean 타입으로 전달해주세요.",
      "path": "/api/v1/users/notifications"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/users/notifications"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_NOT_FOUND",
      "message": "사용자를 찾을 수 없습니다.",
      "path": "/api/v1/users/notifications"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | Request Body가 없거나, 타입이 boolean이 아닌 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | USER_NOT_FOUND | 토큰에 연결된 사용자 ID가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 3. Mission
    
    ---
    
    # 출석체크 현황 조회
    
    Method: GET
    URI patterns: /api/v1/missions/attendance
    완료: No
    
    # 설명 및 권한
    
    지정한 기간 내의 출석체크 기록과 현재 연속 출석 일수를 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/missions/attendance`
    
    ex) `/api/v1/missions/attendance?startDate=2026-07-07&endDate=2026-07-13`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | startDate | string | O | 조회 시작 날짜 (`YYYY-MM-DD` 형식) |
    | endDate | string | O | 조회 종료 날짜 (`YYYY-MM-DD` 형식) |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "continuousAttendance": 5,
        "attendances": [
          { "date": "2026-07-07", "isAttended": true },
          { "date": "2026-07-08", "isAttended": true },
          { "date": "2026-07-09", "isAttended": true },
          { "date": "2026-07-10", "isAttended": false },
          { "date": "2026-07-11", "isAttended": true },
          { "date": "2026-07-12", "isAttended": true },
          { "date": "2026-07-13", "isAttended": false }
        ]
      },
      "message": "출석체크 현황 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | continuousAttendance | number | 현재 연속 출석 일수 |
    | attendances | array | 기간 내 날짜별 출석 여부 리스트 |
    | attendances[].date | string | 대상 날짜 (`YYYY-MM-DD`) |
    | attendances[].isAttended | boolean | 해당 날짜 출석 여부 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "날짜 형식이 올바르지 않습니다.",
      "path": "/api/v1/missions/attendance"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/missions/attendance"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | startDate 또는 endDate 형식이 올바르지 않거나 누락된 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 오늘의 출석체크 기록
    
    Method: POST
    URI patterns: /api/v1/missions/attendance
    완료: No
    
    # 설명 및 권한
    
    오늘 날짜의 출석체크를 기록한다. 이미 출석한 경우 중복 처리되지 않는다. 출석 기록 시 연속 출석 일수가 갱신된다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/missions/attendance`
    
    ex) `/api/v1/missions/attendance`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "continuousAttendance": 6,
        "targetDate": "2026-07-15"
      },
      "message": "출석체크 완료"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | continuousAttendance | number | 출석 후 갱신된 연속 출석 일수 |
    | targetDate | string | 출석 처리된 날짜 (`YYYY-MM-DD`) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/missions/attendance"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_NOT_FOUND",
      "message": "사용자를 찾을 수 없습니다.",
      "path": "/api/v1/missions/attendance"
    }
    ```
    
    ## HTTP Status code: `409 Conflict`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 409,
      "error": "Conflict",
      "code": "ALREADY_ATTENDED",
      "message": "오늘은 이미 출석체크를 완료했습니다.",
      "path": "/api/v1/missions/attendance"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | USER_NOT_FOUND | 토큰에 연결된 사용자 ID가 DB에 존재하지 않는 경우 |
    | 409 Conflict | ALREADY_ATTENDED | 오늘 이미 출석체크를 완료한 경우 |
    
    ---
    
    # 오늘의 미션 달성 및 리워드 수령 상태 조회
    
    Method: GET
    URI patterns: /api/v1/missions/daily
    완료: No
    
    # 설명 및 권한
    
    오늘의 미션 3종(출석체크, 지출 기록, 주사위 굴리기)의 달성 여부와 리워드 수령 여부를 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/missions/daily`
    
    ex) `/api/v1/missions/daily`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "targetDate": "2026-07-15",
        "missions": [
          {
            "missionType": "ATTENDANCE",
            "isDone": true,
            "isRewarded": false
          },
          {
            "missionType": "EXPENSE_RECORD",
            "isDone": false,
            "isRewarded": false
          },
          {
            "missionType": "DICE",
            "isDone": false,
            "isRewarded": false
          }
        ]
      },
      "message": "오늘의 미션 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | targetDate | string | 미션 대상 날짜 (`YYYY-MM-DD`) |
    | missions | array | 미션별 달성 및 리워드 수령 상태 리스트 |
    | missions[].missionType | string | 미션 유형 (`ATTENDANCE`, `EXPENSE_RECORD`, `DICE`) |
    | missions[].isDone | boolean | 미션 달성 여부 |
    | missions[].isRewarded | boolean | 리워드 수령 여부 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/missions/daily"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_NOT_FOUND",
      "message": "사용자를 찾을 수 없습니다.",
      "path": "/api/v1/missions/daily"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | USER_NOT_FOUND | 토큰에 연결된 사용자 ID가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 달성 미션 리워드(코인) 받기
    
    Method: POST
    URI patterns: /api/v1/missions/daily/{missionType}/reward
    완료: No
    
    # 설명 및 권한
    
    달성 완료된 미션의 리워드(1코인)를 수령한다. 미션이 달성되지 않았거나 이미 리워드를 수령한 경우 요청이 거부된다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/missions/daily/{missionType}/reward`
    
    ex) `/api/v1/missions/daily/ATTENDANCE/reward`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Path Variable
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | missionType | string | O | 미션 유형 (`ATTENDANCE`, `EXPENSE_RECORD`, `DICE` 중 하나) |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "missionType": "ATTENDANCE",
        "rewardCoin": 1,
        "coinBalance": 1501
      },
      "message": "미션 리워드 수령 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | missionType | string | 리워드를 수령한 미션 유형 |
    | rewardCoin | number | 수령한 코인 수 |
    | coinBalance | number | 수령 후 현재 코인 잔액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request` (미션 미달성)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "MISSION_NOT_COMPLETED",
      "message": "미션을 아직 달성하지 않았습니다.",
      "path": "/api/v1/missions/daily/ATTENDANCE/reward"
    }
    ```
    
    ## HTTP Status code: `400 Bad Request` (잘못된 미션 유형)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "지원하지 않는 미션 유형입니다.",
      "path": "/api/v1/missions/daily/INVALID/reward"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/missions/daily/ATTENDANCE/reward"
    }
    ```
    
    ## HTTP Status code: `409 Conflict`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 409,
      "error": "Conflict",
      "code": "ALREADY_REWARDED",
      "message": "이미 리워드를 수령했습니다.",
      "path": "/api/v1/missions/daily/ATTENDANCE/reward"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | MISSION_NOT_COMPLETED | 해당 미션이 아직 달성되지 않은 경우 |
    | 400 Bad Request | INVALID_INPUT_VALUE | 지정된 Enum 값 외의 미션 유형이 들어온 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 409 Conflict | ALREADY_REWARDED | 해당 미션의 리워드를 이미 수령한 경우 |
    
    ---
    
    # 4. Finance — 소비
    
    ---
    
    # 오늘의 소비 직접 입력
    
    Method: POST
    URI patterns: /api/v1/finances/expenses
    완료: No
    
    # 설명 및 권한
    
    사용자가 지출 내역을 직접 입력한다. 한 번의 요청으로 여러 건의 지출을 저장할 수 있다. 해당 날짜의 첫 지출 기록이면 오늘의 미션(지출 기록) 달성 처리된다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/finances/expenses`
    
    ex) `/api/v1/finances/expenses`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "expenses": [
        {
          "amount": 4500,
          "placeName": "스타벅스",
          "categoryId": 1,
          "expenseDate": "2026-07-15T14:30:00",
          "memo": "아이스 아메리카노"
        },
        {
          "amount": 8900,
          "placeName": "올리브영",
          "categoryId": 3,
          "expenseDate": "2026-07-15T16:00:00",
          "memo": null
        }
      ]
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | expenses | array | O | 지출 내역 리스트 (1건 이상) |
    | expenses[].amount | number | O | 지출 금액 (양수) |
    | expenses[].placeName | string | O | 사용처 |
    | expenses[].categoryId | number | O | 지출 카테고리 ID (`Expense_Category` 참조) |
    | expenses[].expenseDate | string | O | 지출 날짜 및 시간 (`YYYY-MM-DDTHH:mm:ss` 형식) |
    | expenses[].memo | string | X | 메모 (선택사항) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `201 Created`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "savedCount": 2,
        "isFirstRecordOfDay": true,
        "dailyTotalExpense": 13400
      },
      "message": "지출 기록 저장 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | savedCount | number | 저장된 지출 건수 |
    | isFirstRecordOfDay | boolean | 해당 날짜의 첫 기록 여부 (주사위 돌리기 이동 판단에 사용) |
    | dailyTotalExpense | number | 해당 날짜의 총 지출 금액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "잘못된 입력값입니다. 필수 항목을 확인해주세요.",
      "path": "/api/v1/finances/expenses"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/expenses"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "CATEGORY_NOT_FOUND",
      "message": "존재하지 않는 지출 카테고리입니다.",
      "path": "/api/v1/finances/expenses"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | 필수 항목 누락, 금액이 0 이하, 날짜 형식 오류 등 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | CATEGORY_NOT_FOUND | 요청한 categoryId가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 오늘의 소비 내역 리스트 및 총 지출액 조회
    
    Method: GET
    URI patterns: /api/v1/finances/expenses/daily
    완료: No
    
    # 설명 및 권한
    
    특정 날짜의 소비 내역 리스트와 총 지출액을 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/finances/expenses/daily`
    
    ex) `/api/v1/finances/expenses/daily?date=2026-07-15`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | date | string | O | 조회할 날짜 (`YYYY-MM-DD` 형식) |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "date": "2026-07-15",
        "dailyTotalExpense": 13400,
        "expenses": [
          {
            "expenseId": 101,
            "amount": 4500,
            "placeName": "스타벅스",
            "categoryId": 1,
            "categoryName": "식비",
            "expenseDate": "2026-07-15T14:30:00",
            "memo": "아이스 아메리카노"
          },
          {
            "expenseId": 102,
            "amount": 8900,
            "placeName": "올리브영",
            "categoryId": 3,
            "categoryName": "쇼핑",
            "expenseDate": "2026-07-15T16:00:00",
            "memo": null
          }
        ]
      },
      "message": "일별 소비 내역 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | date | string | 조회된 날짜 |
    | dailyTotalExpense | number | 해당 날짜 총 지출 금액 |
    | expenses | array | 지출 내역 리스트 |
    | expenses[].expenseId | number | 지출 기록 고유 ID |
    | expenses[].amount | number | 지출 금액 |
    | expenses[].placeName | string | 사용처 |
    | expenses[].categoryId | number | 지출 카테고리 ID |
    | expenses[].categoryName | string | 지출 카테고리명 |
    | expenses[].expenseDate | string | 지출 날짜 및 시간 |
    | expenses[].memo | string | 메모 (없으면 null) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "날짜 형식이 올바르지 않습니다.",
      "path": "/api/v1/finances/expenses/daily"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/expenses/daily"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | date 파라미터가 누락되었거나 형식이 올바르지 않은 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 홈 화면 예산 요약 조회
    
    Method: GET
    URI patterns: /api/v1/finances/summary
    완료: No
    
    # 설명 및 권한
    
    이번 달의 남은 예산 금액, 사용 금액, 설정한 예산 금액 및 소진율을 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/finances/summary`
    
    ex) `/api/v1/finances/summary`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "targetMonth": "2026-07",
        "totalExpenseBudget": 500000,
        "totalSpent": 320000,
        "remainingBudget": 180000,
        "usageRate": 64.0
      },
      "message": "예산 요약 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | targetMonth | string | 대상 월 (`YYYY-MM`) |
    | totalExpenseBudget | number | 이번 달 설정한 목표 지출 예산 총액 |
    | totalSpent | number | 이번 달 실제 사용 금액 |
    | remainingBudget | number | 남은 예산 (= totalExpenseBudget - totalSpent) |
    | usageRate | number | 예산 소진율 (%) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/summary"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "MONTHLY_FINANCE_NOT_FOUND",
      "message": "이번 달 예산 설정 정보가 존재하지 않습니다.",
      "path": "/api/v1/finances/summary"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | MONTHLY_FINANCE_NOT_FOUND | 이번 달의 월간 재무 설정 데이터가 없는 경우 |
    
    ---
    
    # 4. Finance — 예산
    
    ---
    
    # 이번 달 예산 설정 전체 현황 조회
    
    Method: GET
    URI patterns: /api/v1/finances/budget
    완료: No
    
    # 설명 및 권한
    
    이번 달의 수입, 저축 목표, 지출 예산 등 예산 설정 전체 현황을 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/finances/budget`
    
    ex) `/api/v1/finances/budget`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "targetMonth": "2026-07",
        "totalIncome": 1500000,
        "targetSavings": 500000,
        "savingsType": "STANDARD",
        "totalExpenseBudget": 800000,
        "incomeDetails": [
          { "incomeDetailId": 1, "category": "용돈", "amount": 1000000 },
          { "incomeDetailId": 2, "category": "알바", "amount": 500000 }
        ],
        "expenseBudgets": [
          { "expenseBudgetId": 1, "categoryId": 1, "categoryName": "식비", "amount": 300000 },
          { "expenseBudgetId": 2, "categoryId": 2, "categoryName": "교통", "amount": 100000 },
          { "expenseBudgetId": 3, "categoryId": 3, "categoryName": "쇼핑", "amount": 200000 },
          { "expenseBudgetId": 4, "categoryId": 4, "categoryName": "문화", "amount": 200000 }
        ]
      },
      "message": "예산 설정 현황 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | targetMonth | string | 대상 월 (`YYYY-MM`) |
    | totalIncome | number | 한 달 총 수입 |
    | targetSavings | number | 저축 목표 금액 |
    | savingsType | string | 저축 유형 (`SAVING`, `STANDARD`, `CHALLENGE`) |
    | totalExpenseBudget | number | 한 달 목표 지출 예산 총액 |
    | incomeDetails | array | 카테고리별 수입 내역 리스트 |
    | incomeDetails[].incomeDetailId | number | 수입 내역 고유 ID |
    | incomeDetails[].category | string | 수입 카테고리명 |
    | incomeDetails[].amount | number | 해당 카테고리 수입 금액 |
    | expenseBudgets | array | 카테고리별 목표 지출 예산 리스트 |
    | expenseBudgets[].expenseBudgetId | number | 목표 지출 예산 고유 ID |
    | expenseBudgets[].categoryId | number | 지출 카테고리 ID |
    | expenseBudgets[].categoryName | string | 지출 카테고리명 |
    | expenseBudgets[].amount | number | 해당 카테고리 목표 지출 금액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/budget"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "MONTHLY_FINANCE_NOT_FOUND",
      "message": "이번 달 예산 설정 정보가 존재하지 않습니다.",
      "path": "/api/v1/finances/budget"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | MONTHLY_FINANCE_NOT_FOUND | 이번 달의 월간 재무 설정 데이터가 없는 경우 |
    
    ---
    
    # 이번 달 카테고리별 수입 총합 및 내역 수정
    
    Method: PUT
    URI patterns: /api/v1/finances/income
    완료: No
    
    # 설명 및 권한
    
    이번 달의 카테고리별 수입 내역을 전체 교체(덮어쓰기)한다. 기존 수입 내역은 삭제되고 요청된 내역으로 새로 저장된다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `PUT /api/v1/finances/income`
    
    ex) `/api/v1/finances/income`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "incomeDetails": [
        { "category": "용돈", "amount": 1000000 },
        { "category": "알바", "amount": 600000 }
      ]
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | incomeDetails | array | O | 카테고리별 수입 내역 리스트 (1건 이상) |
    | incomeDetails[].category | string | O | 수입 카테고리명 |
    | incomeDetails[].amount | number | O | 해당 카테고리 수입 금액 (0 이상) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "totalIncome": 1600000
      },
      "message": "수입 내역 수정 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | totalIncome | number | 수정 후 한 달 총 수입 합계 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "잘못된 입력값입니다. 금액은 0 이상이어야 합니다.",
      "path": "/api/v1/finances/income"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/income"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "MONTHLY_FINANCE_NOT_FOUND",
      "message": "이번 달 예산 설정 정보가 존재하지 않습니다.",
      "path": "/api/v1/finances/income"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | 필수 항목 누락 또는 금액이 음수인 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | MONTHLY_FINANCE_NOT_FOUND | 이번 달의 월간 재무 설정 데이터가 없는 경우 |
    
    ---
    
    # 이번 달 저축 목표 금액 및 저축 유형 수정
    
    Method: PUT
    URI patterns: /api/v1/finances/savings
    완료: No
    
    # 설명 및 권한
    
    이번 달의 저축 목표 금액을 수정한다. 저축 유형은 총 수입 대비 저축 목표 비율에 따라 자동 결정된다. (0~30%: SAVING, ~50%: STANDARD, 그 이상: CHALLENGE)
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `PUT /api/v1/finances/savings`
    
    ex) `/api/v1/finances/savings`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "targetSavings": 500000
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | targetSavings | number | O | 저축 목표 금액 (0 이상) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "targetSavings": 500000,
        "savingsType": "STANDARD"
      },
      "message": "저축 목표 수정 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | targetSavings | number | 수정된 저축 목표 금액 |
    | savingsType | string | 자동 결정된 저축 유형 (`SAVING`, `STANDARD`, `CHALLENGE`) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "잘못된 입력값입니다. 금액은 0 이상이어야 합니다.",
      "path": "/api/v1/finances/savings"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/savings"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "MONTHLY_FINANCE_NOT_FOUND",
      "message": "이번 달 예산 설정 정보가 존재하지 않습니다.",
      "path": "/api/v1/finances/savings"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | targetSavings가 누락되었거나 음수인 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | MONTHLY_FINANCE_NOT_FOUND | 이번 달의 월간 재무 설정 데이터가 없는 경우 |
    
    ---
    
    # 한 달 목표 총 지출 예산 및 카테고리별 목표 지출 예산 수정
    
    Method: PUT
    URI patterns: /api/v1/finances/expense-budget
    완료: No
    
    # 설명 및 권한
    
    한 달 목표 총 지출 예산과 카테고리별 목표 지출 예산을 수정한다. 카테고리별 지출 예산 합계가 총 지출 예산과 일치해야 한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `PUT /api/v1/finances/expense-budget`
    
    ex) `/api/v1/finances/expense-budget`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "totalExpenseBudget": 800000,
      "expenseBudgets": [
        { "categoryId": 1, "amount": 300000 },
        { "categoryId": 2, "amount": 100000 },
        { "categoryId": 3, "amount": 200000 },
        { "categoryId": 4, "amount": 200000 }
      ]
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | totalExpenseBudget | number | O | 한 달 목표 총 지출 예산 |
    | expenseBudgets | array | O | 카테고리별 목표 지출 예산 리스트 |
    | expenseBudgets[].categoryId | number | O | 지출 카테고리 ID |
    | expenseBudgets[].amount | number | O | 해당 카테고리 목표 지출 금액 (0 이상) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": null,
      "message": "지출 예산 수정 성공"
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request` (입력값 오류)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "잘못된 입력값입니다.",
      "path": "/api/v1/finances/expense-budget"
    }
    ```
    
    ## HTTP Status code: `400 Bad Request` (합계 불일치)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "BUDGET_SUM_MISMATCH",
      "message": "카테고리별 지출 예산 합계가 총 지출 예산과 일치하지 않습니다.",
      "path": "/api/v1/finances/expense-budget"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/expense-budget"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "MONTHLY_FINANCE_NOT_FOUND",
      "message": "이번 달 예산 설정 정보가 존재하지 않습니다.",
      "path": "/api/v1/finances/expense-budget"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | 필수 항목 누락 또는 금액이 음수인 경우 |
    | 400 Bad Request | BUDGET_SUM_MISMATCH | 카테고리별 지출 예산 합계 ≠ totalExpenseBudget인 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | MONTHLY_FINANCE_NOT_FOUND | 이번 달의 월간 재무 설정 데이터가 없는 경우 |
    
    ---
    
    # 지난달 예산 계획 복사 데이터 조회
    
    Method: GET
    URI patterns: /api/v1/finances/expense-budget/copy-last-month
    완료: No
    
    # 설명 및 권한
    
    지난달의 카테고리별 목표 지출 예산을 조회한다. 이번 달 예산 편집 시 "지난달 계획 복사하기" 기능에서 사용된다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/finances/expense-budget/copy-last-month`
    
    ex) `/api/v1/finances/expense-budget/copy-last-month`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "sourceMonth": "2026-06",
        "totalExpenseBudget": 750000,
        "expenseBudgets": [
          { "categoryId": 1, "categoryName": "식비", "amount": 300000 },
          { "categoryId": 2, "categoryName": "교통", "amount": 80000 },
          { "categoryId": 3, "categoryName": "쇼핑", "amount": 170000 },
          { "categoryId": 4, "categoryName": "문화", "amount": 200000 }
        ]
      },
      "message": "지난달 예산 계획 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | sourceMonth | string | 복사 대상 월 (`YYYY-MM`) |
    | totalExpenseBudget | number | 지난달 목표 지출 예산 총액 |
    | expenseBudgets | array | 지난달 카테고리별 목표 지출 예산 리스트 |
    | expenseBudgets[].categoryId | number | 지출 카테고리 ID |
    | expenseBudgets[].categoryName | string | 지출 카테고리명 |
    | expenseBudgets[].amount | number | 해당 카테고리 목표 지출 금액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/expense-budget/copy-last-month"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "MONTHLY_FINANCE_NOT_FOUND",
      "message": "지난달 예산 설정 정보가 존재하지 않습니다.",
      "path": "/api/v1/finances/expense-budget/copy-last-month"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | MONTHLY_FINANCE_NOT_FOUND | 지난달의 월간 재무 설정 데이터가 없는 경우 |
    
    ---
    
    # 지출 카테고리 목록 조회
    
    Method: GET
    URI patterns: /api/v1/finances/expense-categories
    완료: No
    
    # 설명 및 권한
    
    기본 제공 카테고리와 사용자가 추가한 커스텀 카테고리를 모두 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/finances/expense-categories`
    
    ex) `/api/v1/finances/expense-categories`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "categories": [
          { "categoryId": 1, "name": "식비", "isDefault": true },
          { "categoryId": 2, "name": "교통", "isDefault": true },
          { "categoryId": 3, "name": "쇼핑", "isDefault": true },
          { "categoryId": 4, "name": "문화", "isDefault": true },
          { "categoryId": 10, "name": "덕질", "isDefault": false }
        ]
      },
      "message": "지출 카테고리 목록 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | categories | array | 지출 카테고리 리스트 |
    | categories[].categoryId | number | 카테고리 고유 ID |
    | categories[].name | string | 카테고리명 |
    | categories[].isDefault | boolean | 기본 제공 카테고리 여부 (true: 기본, false: 유저 추가) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/expense-categories"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 사용자 커스텀 지출 카테고리 추가
    
    Method: POST
    URI patterns: /api/v1/finances/expense-categories
    완료: No
    
    # 설명 및 권한
    
    사용자가 직접 지출 카테고리를 추가한다. 기본 카테고리와 중복되는 이름은 허용되지 않는다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/finances/expense-categories`
    
    ex) `/api/v1/finances/expense-categories`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "name": "덕질"
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | name | string | O | 추가할 카테고리명 |
    
    # ✅ Success Response
    
    ## HTTP Status code: `201 Created`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "categoryId": 10,
        "name": "덕질",
        "isDefault": false
      },
      "message": "카테고리 추가 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | categoryId | number | 생성된 카테고리 고유 ID |
    | name | string | 카테고리명 |
    | isDefault | boolean | 기본 제공 여부 (항상 false) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "카테고리명이 비어있습니다.",
      "path": "/api/v1/finances/expense-categories"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/expense-categories"
    }
    ```
    
    ## HTTP Status code: `409 Conflict`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 409,
      "error": "Conflict",
      "code": "DUPLICATE_CATEGORY_NAME",
      "message": "이미 존재하는 카테고리명입니다.",
      "path": "/api/v1/finances/expense-categories"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | 카테고리명이 비어있거나 형식이 잘못된 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 409 Conflict | DUPLICATE_CATEGORY_NAME | 동일한 이름의 카테고리가 이미 존재하는 경우 |
    
    ---
    
    # 사용자 커스텀 지출 카테고리 삭제
    
    Method: DELETE
    URI patterns: /api/v1/finances/expense-categories/{categoryId}
    완료: No
    
    # 설명 및 권한
    
    사용자가 추가한 커스텀 지출 카테고리를 삭제한다. 기본 제공 카테고리는 삭제할 수 없다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `DELETE /api/v1/finances/expense-categories/{categoryId}`
    
    ex) `/api/v1/finances/expense-categories/10`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Path Variable
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | categoryId | number | O | 삭제할 카테고리 ID |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": null,
      "message": "카테고리 삭제 성공"
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "DEFAULT_CATEGORY_NOT_MODIFIABLE",
      "message": "기본 제공 카테고리는 삭제할 수 없습니다.",
      "path": "/api/v1/finances/expense-categories/1"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/finances/expense-categories/10"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "CATEGORY_NOT_FOUND",
      "message": "존재하지 않는 카테고리입니다.",
      "path": "/api/v1/finances/expense-categories/999"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | DEFAULT_CATEGORY_NOT_MODIFIABLE | 기본 제공 카테고리(isDefault=true)를 삭제하려는 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | CATEGORY_NOT_FOUND | 해당 categoryId가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 5. Quiz
    
    ---
    
    # 오늘의 O/X 퀴즈 내용 조회
    
    Method: GET
    URI patterns: /api/v1/quiz/daily
    완료: No
    
    # 설명 및 권한
    
    홈 화면에 표시할 오늘의 O/X 퀴즈 문제를 조회한다. 이미 답을 제출한 경우 제출 결과와 해설도 함께 반환한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/quiz/daily`
    
    ex) `/api/v1/quiz/daily`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK` (아직 풀지 않은 경우)
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "quizId": 42,
        "question": "적금은 만기 전에 해지하면 약정 이율을 받을 수 있다.",
        "isSubmitted": false
      },
      "message": "오늘의 퀴즈 조회 성공"
    }
    ```
    
    ## HTTP Status code: `200 OK` (이미 제출한 경우)
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "quizId": 42,
        "question": "적금은 만기 전에 해지하면 약정 이율을 받을 수 있다.",
        "isSubmitted": true,
        "isCorrect": false,
        "correctAnswer": "X",
        "explanation": "적금을 만기 전에 해지하면 중도해지 이율이 적용되어 약정 이율보다 낮은 이자를 받게 됩니다."
      },
      "message": "오늘의 퀴즈 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | quizId | number | 퀴즈 고유 ID |
    | question | string | 퀴즈 문제 |
    | isSubmitted | boolean | 답 제출 여부 |
    | isCorrect | boolean | 정답 여부 (제출 후에만 포함) |
    | correctAnswer | string | 정답 (`O` 또는 `X`, 제출 후에만 포함) |
    | explanation | string | 해설 (제출 후에만 포함) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/quiz/daily"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 오늘의 O/X 퀴즈 정답 제출
    
    Method: POST
    URI patterns: /api/v1/quiz/daily/submit
    완료: No
    
    # 설명 및 권한
    
    오늘의 O/X 퀴즈에 대한 답을 제출한다. 정답이면 1코인을 획득한다. 한 번만 제출할 수 있다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/quiz/daily/submit`
    
    ex) `/api/v1/quiz/daily/submit`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "quizId": 42,
      "answer": "X"
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | quizId | number | O | 퀴즈 고유 ID |
    | answer | string | O | 사용자가 선택한 답 (`O` 또는 `X`) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "isCorrect": true,
        "correctAnswer": "X",
        "explanation": "적금을 만기 전에 해지하면 중도해지 이율이 적용되어 약정 이율보다 낮은 이자를 받게 됩니다.",
        "rewardCoin": 1,
        "coinBalance": 1502
      },
      "message": "퀴즈 정답 제출 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | isCorrect | boolean | 정답 여부 |
    | correctAnswer | string | 정답 (`O` 또는 `X`) |
    | explanation | string | 퀴즈 해설 |
    | rewardCoin | number | 획득한 코인 수 (정답: 1, 오답: 0) |
    | coinBalance | number | 획득 후 현재 코인 잔액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "답은 O 또는 X만 입력할 수 있습니다.",
      "path": "/api/v1/quiz/daily/submit"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/quiz/daily/submit"
    }
    ```
    
    ## HTTP Status code: `409 Conflict`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 409,
      "error": "Conflict",
      "code": "ALREADY_SUBMITTED",
      "message": "오늘의 퀴즈는 이미 제출했습니다.",
      "path": "/api/v1/quiz/daily/submit"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | answer가 O 또는 X가 아니거나 필수 항목이 누락된 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 409 Conflict | ALREADY_SUBMITTED | 오늘의 퀴즈에 이미 답을 제출한 경우 |
    
    ---
    
    # 맵 화면 주사위용 4지선다 금융 퀴즈 조회
    
    Method: GET
    URI patterns: /api/v1/quiz/finance
    완료: No
    
    # 설명 및 권한
    
    맵 화면에서 주사위를 굴리기 전에 풀어야 하는 4지선다 금융 퀴즈를 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/quiz/finance`
    
    ex) `/api/v1/quiz/finance`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "quizId": 88,
        "question": "다음 중 복리 이자가 적용되는 금융 상품은?",
        "options": [
          { "optionNumber": 1, "content": "보통예금" },
          { "optionNumber": 2, "content": "정기적금" },
          { "optionNumber": 3, "content": "CMA" },
          { "optionNumber": 4, "content": "MMF" }
        ]
      },
      "message": "금융 퀴즈 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | quizId | number | 퀴즈 고유 ID |
    | question | string | 퀴즈 문제 |
    | options | array | 선택지 리스트 (4개) |
    | options[].optionNumber | number | 선택지 번호 (1~4) |
    | options[].content | string | 선택지 내용 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/quiz/finance"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 4지선다 금융 퀴즈 정답 제출
    
    Method: POST
    URI patterns: /api/v1/quiz/finance/submit
    완료: No
    
    # 설명 및 권한
    
    4지선다 금융 퀴즈에 대한 답을 제출한다. 정답이면 1코인을 획득하고 주사위 굴리기가 활성화된다. 오답이면 광고 시청 후 재도전하거나 정답을 열람할 수 있다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/quiz/finance/submit`
    
    ex) `/api/v1/quiz/finance/submit`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "quizId": 88,
      "selectedOption": 2
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | quizId | number | O | 퀴즈 고유 ID |
    | selectedOption | number | O | 선택한 선택지 번호 (1~4) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "isCorrect": true,
        "correctOption": 2,
        "explanation": "정기적금은 매월 일정 금액을 불입하며, 복리 이자가 적용되어 만기 시 더 높은 수익을 기대할 수 있습니다.",
        "rewardCoin": 1,
        "coinBalance": 1503,
        "isDiceEnabled": true
      },
      "message": "금융 퀴즈 정답 제출 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | isCorrect | boolean | 정답 여부 |
    | correctOption | number | 정답 선택지 번호 |
    | explanation | string | 퀴즈 해설 |
    | rewardCoin | number | 획득한 코인 수 (정답: 1, 오답: 0) |
    | coinBalance | number | 획득 후 현재 코인 잔액 |
    | isDiceEnabled | boolean | 주사위 굴리기 활성화 여부 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "선택지 번호는 1~4 사이여야 합니다.",
      "path": "/api/v1/quiz/finance/submit"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/quiz/finance/submit"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "QUIZ_NOT_FOUND",
      "message": "존재하지 않는 퀴즈입니다.",
      "path": "/api/v1/quiz/finance/submit"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | 선택지 번호가 1~4 범위 밖이거나 필수 항목이 누락된 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | QUIZ_NOT_FOUND | 해당 quizId가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 6. Map
    
    ---
    
    # 주사위 굴리기 실행 및 맵 위치 이동
    
    Method: POST
    URI patterns: /api/v1/map/dice
    완료: No
    
    # 설명 및 권한
    
    주사위를 굴려 나온 결과만큼 맵 위치를 이동시킨다. 도착 칸의 종류에 따라 일반, 보상(코인 획득), 패널티(칸 이동 또는 처음으로) 규칙이 자동 적용된다. 주사위 굴리기 미션이 달성 처리된다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/map/dice`
    
    ex) `/api/v1/map/dice`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "diceResult": 4,
        "previousPosition": 5,
        "landedPosition": 9,
        "finalPosition": 9,
        "event": {
          "eventType": "NORMAL",
          "description": "일반 칸입니다."
        }
      },
      "message": "주사위 굴리기 성공"
    }
    ```
    
    ### Content (보상 칸에 도착한 경우)
    
    ```json
    {
      "success": true,
      "data": {
        "diceResult": 3,
        "previousPosition": 4,
        "landedPosition": 7,
        "finalPosition": 7,
        "event": {
          "eventType": "TREASURE",
          "description": "보물상자를 발견했습니다!",
          "rewardCoin": 3,
          "coinBalance": 1506
        }
      },
      "message": "주사위 굴리기 성공"
    }
    ```
    
    ### Content (패널티 칸에 도착한 경우)
    
    ```json
    {
      "success": true,
      "data": {
        "diceResult": 5,
        "previousPosition": 5,
        "landedPosition": 10,
        "finalPosition": 1,
        "event": {
          "eventType": "RESET",
          "description": "처음으로 돌아갑니다!"
        }
      },
      "message": "주사위 굴리기 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | diceResult | number | 주사위 결과 (1~6) |
    | previousPosition | number | 이동 전 위치 |
    | landedPosition | number | 주사위 결과로 도착한 칸 번호 |
    | finalPosition | number | 이벤트 적용 후 최종 위치 (패널티 이동 반영) |
    | event.eventType | string | 이벤트 종류 (`NORMAL`, `TREASURE`, `BACK`, `RESET`, `FINISH`) |
    | event.description | string | 이벤트 설명 |
    | event.rewardCoin | number | 보상 코인 수 (보상 칸에서만 포함) |
    | event.coinBalance | number | 보상 후 코인 잔액 (보상 칸에서만 포함) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/map/dice"
    }
    ```
    
    ## HTTP Status code: `403 Forbidden`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 403,
      "error": "Forbidden",
      "code": "DICE_NOT_ENABLED",
      "message": "주사위를 굴릴 수 없습니다. 금융 퀴즈를 먼저 풀어주세요.",
      "path": "/api/v1/map/dice"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_NOT_FOUND",
      "message": "사용자를 찾을 수 없습니다.",
      "path": "/api/v1/map/dice"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 403 Forbidden | DICE_NOT_ENABLED | 금융 퀴즈를 풀지 않아 주사위 굴리기가 비활성화된 경우 |
    | 404 Not Found | USER_NOT_FOUND | 토큰에 연결된 사용자 ID가 DB에 존재하지 않는 경우 |
    
    ---
    
    # 7. Ads
    
    ---
    
    # 광고 시청 후 코인 2배 받기
    
    Method: POST
    URI patterns: /api/v1/ads/reward
    완료: No
    
    # 설명 및 권한
    
    사용자가 광고 시청을 완료한 뒤 기존에 획득한 코인을 2배로 받는다. 클라이언트에서 광고 시청 완료 후 호출한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/ads/reward`
    
    ex) `/api/v1/ads/reward`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "rewardType": "EXPENSE_RECORD",
      "adId": "ad_unit_12345"
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | rewardType | string | O | 보상 대상 (`EXPENSE_RECORD`, `FINANCE_QUIZ` 중 하나) |
    | adId | string | O | 시청한 광고 단위 식별자 |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "bonusCoin": 1,
        "coinBalance": 1504
      },
      "message": "광고 보상 지급 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | bonusCoin | number | 추가 지급된 보너스 코인 수 |
    | coinBalance | number | 지급 후 현재 코인 잔액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "잘못된 보상 유형입니다.",
      "path": "/api/v1/ads/reward"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/ads/reward"
    }
    ```
    
    ## HTTP Status code: `409 Conflict`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 409,
      "error": "Conflict",
      "code": "ALREADY_REWARDED",
      "message": "이미 광고 보상을 수령했습니다.",
      "path": "/api/v1/ads/reward"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | rewardType이 지정된 값이 아니거나 필수 항목이 누락된 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 409 Conflict | ALREADY_REWARDED | 해당 활동에 대한 광고 보상을 이미 수령한 경우 |
    
    ---
    
    # 8. Group
    
    ---
    
    # 신규 그룹 생성
    
    Method: POST
    URI patterns: /api/v1/groups
    완료: No
    
    # 설명 및 권한
    
    새로운 그룹을 생성하고 랜덤 초대 코드를 발급한다. 그룹 생성자는 자동으로 해당 그룹의 멤버로 추가된다. 그룹은 최대 4개까지 생성할 수 있다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/groups`
    
    ex) `/api/v1/groups`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `201 Created`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "groupId": 5,
        "inviteCode": "ABC12XYZ"
      },
      "message": "그룹 생성 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | groupId | number | 생성된 그룹 고유 ID |
    | inviteCode | string | 초대 링크용 랜덤 코드 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "GROUP_LIMIT_EXCEEDED",
      "message": "그룹은 최대 4개까지 생성할 수 있습니다.",
      "path": "/api/v1/groups"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/groups"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | GROUP_LIMIT_EXCEEDED | 이미 4개의 그룹에 속해 있는 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 초대 코드로 그룹 가입
    
    Method: POST
    URI patterns: /api/v1/groups/join
    완료: No
    
    # 설명 및 권한
    
    전달받은 초대 코드로 해당 그룹에 멤버로 가입한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/groups/join`
    
    ex) `/api/v1/groups/join`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    {
      "inviteCode": "ABC12XYZ"
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | inviteCode | string | O | 그룹 초대 코드 |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "groupId": 5
      },
      "message": "그룹 가입 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | groupId | number | 가입한 그룹 고유 ID |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "GROUP_LIMIT_EXCEEDED",
      "message": "그룹은 최대 4개까지 가입할 수 있습니다.",
      "path": "/api/v1/groups/join"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/groups/join"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "GROUP_NOT_FOUND",
      "message": "유효하지 않은 초대 코드입니다.",
      "path": "/api/v1/groups/join"
    }
    ```
    
    ## HTTP Status code: `409 Conflict`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 409,
      "error": "Conflict",
      "code": "ALREADY_JOINED",
      "message": "이미 가입된 그룹입니다.",
      "path": "/api/v1/groups/join"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | GROUP_LIMIT_EXCEEDED | 이미 4개의 그룹에 속해 있는 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | GROUP_NOT_FOUND | 해당 초대 코드에 해당하는 그룹이 없는 경우 |
    | 409 Conflict | ALREADY_JOINED | 해당 그룹에 이미 가입되어 있는 경우 |
    
    ---
    
    # 내가 속한 그룹 내 구성원 현황 조회
    
    Method: GET
    URI patterns: /api/v1/groups
    완료: No
    
    # 설명 및 권한
    
    내가 속한 그룹과 각 그룹 내 구성원들의 캐릭터 정보 및 맵 위치를 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/groups`
    
    ex) `/api/v1/groups`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "groups": [
          {
            "groupId": 5,
            "inviteCode": "ABC12XYZ",
            "members": [
              {
                "userId": 1,
                "nickname": "델타초보",
                "bodyColor": "PINK",
                "eyeShape": "HAPPY",
                "mapPosition": 15,
                "equippedItems": [
                  { "itemId": 101, "itemType": "TOP" }
                ]
              },
              {
                "userId": 2,
                "nickname": "절약왕",
                "bodyColor": "SKYBLUE",
                "eyeShape": "WINK",
                "mapPosition": 22,
                "equippedItems": []
              }
            ]
          }
        ]
      },
      "message": "그룹 현황 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | groups | array | 가입한 그룹 리스트 |
    | groups[].groupId | number | 그룹 고유 ID |
    | groups[].inviteCode | string | 초대 코드 |
    | groups[].members | array | 그룹 내 구성원 리스트 |
    | groups[].members[].userId | number | 사용자 ID |
    | groups[].members[].nickname | string | 캐릭터 닉네임 |
    | groups[].members[].bodyColor | string | 캐릭터 몸통 색상 |
    | groups[].members[].eyeShape | string | 캐릭터 눈 모양 |
    | groups[].members[].mapPosition | number | 현재 맵 위치 |
    | groups[].members[].equippedItems | array | 장착 중인 아이템 리스트 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/groups"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 그룹 탈퇴
    
    Method: DELETE
    URI patterns: /api/v1/groups/{groupId}/leave
    완료: No
    
    # 설명 및 권한
    
    속한 그룹에서 탈퇴한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `DELETE /api/v1/groups/{groupId}/leave`
    
    ex) `/api/v1/groups/5/leave`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Path Variable
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | groupId | number | O | 탈퇴할 그룹 ID |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": null,
      "message": "그룹 탈퇴 성공"
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/groups/5/leave"
    }
    ```
    
    ## HTTP Status code: `404 Not Found` (그룹 없음)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "GROUP_NOT_FOUND",
      "message": "존재하지 않는 그룹입니다.",
      "path": "/api/v1/groups/999/leave"
    }
    ```
    
    ## HTTP Status code: `404 Not Found` (멤버 아님)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "GROUP_MEMBER_NOT_FOUND",
      "message": "해당 그룹에 가입되어 있지 않습니다.",
      "path": "/api/v1/groups/5/leave"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | GROUP_NOT_FOUND | 해당 groupId의 그룹이 존재하지 않는 경우 |
    | 404 Not Found | GROUP_MEMBER_NOT_FOUND | 사용자가 해당 그룹에 가입되어 있지 않은 경우 |
    
    ---
    
    # 9. Item
    
    ---
    
    # 코인 상점 아이템 리스트 및 가격 조회
    
    Method: GET
    URI patterns: /api/v1/items/shop
    완료: No
    
    # 설명 및 권한
    
    코인 상점에서 구매할 수 있는 의상/장식 아이템 리스트와 가격을 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/items/shop`
    
    ex) `/api/v1/items/shop`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "coinBalance": 1500,
        "items": [
          {
            "itemId": 101,
            "name": "줄무늬 티셔츠",
            "price": 10,
            "itemType": "TOP",
            "isOwned": true
          },
          {
            "itemId": 201,
            "name": "멋진 선글라스",
            "price": 15,
            "itemType": "GLASSES",
            "isOwned": false
          },
          {
            "itemId": 301,
            "name": "빨간 모자",
            "price": 20,
            "itemType": "HAT",
            "isOwned": false
          }
        ]
      },
      "message": "상점 아이템 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | coinBalance | number | 현재 보유 코인 잔액 |
    | items | array | 상점 아이템 리스트 |
    | items[].itemId | number | 아이템 고유 ID |
    | items[].name | string | 아이템 이름 |
    | items[].price | number | 구매에 필요한 코인 가격 |
    | items[].itemType | string | 아이템 종류 (`TOP`, `BOTTOM`, `GLASSES`, `HAT`) |
    | items[].isOwned | boolean | 사용자의 보유 여부 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/items/shop"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 아이템 구매
    
    Method: POST
    URI patterns: /api/v1/items/{itemId}/buy
    완료: No
    
    # 설명 및 권한
    
    보유 코인을 차감하여 아이템을 구매한다. 이미 보유한 아이템은 중복 구매할 수 없다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `POST /api/v1/items/{itemId}/buy`
    
    ex) `/api/v1/items/201/buy`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Path Variable
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | itemId | number | O | 구매할 아이템 ID |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "itemId": 201,
        "itemName": "멋진 선글라스",
        "price": 15,
        "coinBalance": 1485
      },
      "message": "아이템 구매 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | itemId | number | 구매한 아이템 ID |
    | itemName | string | 구매한 아이템 이름 |
    | price | number | 차감된 코인 가격 |
    | coinBalance | number | 구매 후 코인 잔액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INSUFFICIENT_COIN",
      "message": "코인이 부족합니다.",
      "path": "/api/v1/items/201/buy"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/items/201/buy"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "ITEM_NOT_FOUND",
      "message": "존재하지 않는 아이템입니다.",
      "path": "/api/v1/items/999/buy"
    }
    ```
    
    ## HTTP Status code: `409 Conflict`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 409,
      "error": "Conflict",
      "code": "ALREADY_OWNED",
      "message": "이미 보유 중인 아이템입니다.",
      "path": "/api/v1/items/201/buy"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INSUFFICIENT_COIN | 보유 코인이 아이템 가격보다 적은 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | ITEM_NOT_FOUND | 해당 itemId의 아이템이 존재하지 않는 경우 |
    | 409 Conflict | ALREADY_OWNED | 이미 보유 중인 아이템을 구매하려는 경우 |
    
    ---
    
    # 내 아이템 리스트 조회
    
    Method: GET
    URI patterns: /api/v1/items/my
    완료: No
    
    # 설명 및 권한
    
    사용자가 소유 중인 아이템 리스트와 장착 여부를 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/items/my`
    
    ex) `/api/v1/items/my`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "items": [
          {
            "itemId": 101,
            "name": "줄무늬 티셔츠",
            "itemType": "TOP",
            "isEquipped": true
          },
          {
            "itemId": 205,
            "name": "베레모",
            "itemType": "HAT",
            "isEquipped": true
          },
          {
            "itemId": 301,
            "name": "빨간 모자",
            "itemType": "HAT",
            "isEquipped": false
          }
        ]
      },
      "message": "내 아이템 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | items | array | 보유 아이템 리스트 |
    | items[].itemId | number | 아이템 고유 ID |
    | items[].name | string | 아이템 이름 |
    | items[].itemType | string | 아이템 종류 (`TOP`, `BOTTOM`, `GLASSES`, `HAT`) |
    | items[].isEquipped | boolean | 현재 장착 여부 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/items/my"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 아이템 착용/벗기
    
    Method: PATCH
    URI patterns: /api/v1/items/my/{itemId}/equip
    완료: No
    
    # 설명 및 권한
    
    보유한 아이템을 착용하거나 벗는다. 착용(equip=true) 시 같은 종류의 기존 장착 아이템은 자동으로 해제된다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `PATCH /api/v1/items/my/{itemId}/equip`
    
    ex) `/api/v1/items/my/101/equip`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Path Variable
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | itemId | number | O | 착용/벗기 할 아이템 ID |
    
    ## Request Body
    
    ```json
    {
      "equip": true
    }
    ```
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | equip | boolean | O | 장착 여부 (true: 착용, false: 벗기) |
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": null,
      "message": "아이템 착용 상태 변경 성공"
    }
    ```
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/items/my/101/equip"
    }
    ```
    
    ## HTTP Status code: `404 Not Found` (아이템 없음)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "ITEM_NOT_FOUND",
      "message": "존재하지 않는 아이템입니다.",
      "path": "/api/v1/items/my/999/equip"
    }
    ```
    
    ## HTTP Status code: `404 Not Found` (미보유)
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "USER_ITEM_NOT_FOUND",
      "message": "보유하지 않은 아이템입니다.",
      "path": "/api/v1/items/my/201/equip"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | ITEM_NOT_FOUND | 해당 itemId의 아이템이 DB에 존재하지 않는 경우 |
    | 404 Not Found | USER_ITEM_NOT_FOUND | 사용자가 해당 아이템을 보유하고 있지 않은 경우 |
    
    ---
    
    # 10. Shop
    
    ---
    
    # 코인 패키지 리스트 조회
    
    Method: GET
    URI patterns: /api/v1/shop/coins
    완료: No
    
    # 설명 및 권한
    
    현금으로 구매할 수 있는 코인 패키지 리스트를 조회한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/shop/coins`
    
    ex) `/api/v1/shop/coins`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "coinBalance": 1500,
        "packages": [
          { "packageId": 1, "coinAmount": 10, "bonusCoin": 0, "price": 1000 },
          { "packageId": 2, "coinAmount": 30, "bonusCoin": 0, "price": 3000 },
          { "packageId": 3, "coinAmount": 50, "bonusCoin": 0, "price": 5000 },
          { "packageId": 4, "coinAmount": 100, "bonusCoin": 10, "price": 10000 },
          { "packageId": 5, "coinAmount": 300, "bonusCoin": 50, "price": 30000 }
        ]
      },
      "message": "코인 패키지 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | coinBalance | number | 현재 보유 코인 잔액 |
    | packages | array | 코인 패키지 리스트 |
    | packages[].packageId | number | 패키지 고유 ID |
    | packages[].coinAmount | number | 기본 제공 코인 수 |
    | packages[].bonusCoin | number | 보너스 코인 수 |
    | packages[].price | number | 결제 금액 (원) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/shop/coins"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 11. Report
    
    ---
    
    # 주간 리포트 조회
    
    Method: GET
    URI patterns: /api/v1/reports/weekly
    완료: No
    
    # 설명 및 권한
    
    지정한 날짜가 속한 주(월~일)의 주간 소비 리포트를 조회한다. 요일별 지출, 지난주 대비 비교, 지출 1위 카테고리, 또래 대비 소비 순위 등을 포함한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/reports/weekly`
    
    ex) `/api/v1/reports/weekly?date=2026-07-14`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | date | string | O | 조회할 주에 속하는 임의의 날짜 (`YYYY-MM-DD` 형식) |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "weekStartDate": "2026-07-06",
        "weekEndDate": "2026-07-12",
        "dailyExpenses": [
          { "dayOfWeek": "MON", "date": "2026-07-06", "amount": 15000 },
          { "dayOfWeek": "TUE", "date": "2026-07-07", "amount": 8000 },
          { "dayOfWeek": "WED", "date": "2026-07-08", "amount": 22000 },
          { "dayOfWeek": "THU", "date": "2026-07-09", "amount": 5000 },
          { "dayOfWeek": "FRI", "date": "2026-07-10", "amount": 35000 },
          { "dayOfWeek": "SAT", "date": "2026-07-11", "amount": 42000 },
          { "dayOfWeek": "SUN", "date": "2026-07-12", "amount": 12000 }
        ],
        "weeklyTotalExpense": 139000,
        "maxExpenseDay": "SAT",
        "lastWeekComparison": {
          "lastWeekTotalExpense": 120000,
          "changeAmount": 19000,
          "changeRate": 15.8
        },
        "topCategory": {
          "categoryId": 1,
          "categoryName": "식비",
          "amount": 65000
        },
        "peerRanking": {
          "percentile": 35
        },
        "categoryExpenses": [
          { "categoryId": 1, "categoryName": "식비", "amount": 65000, "percentage": 46.8 },
          { "categoryId": 2, "categoryName": "교통", "amount": 30000, "percentage": 21.6 },
          { "categoryId": 3, "categoryName": "쇼핑", "amount": 24000, "percentage": 17.3 },
          { "categoryId": 4, "categoryName": "문화", "amount": 20000, "percentage": 14.3 }
        ]
      },
      "message": "주간 리포트 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | weekStartDate | string | 주 시작 날짜 (월요일) |
    | weekEndDate | string | 주 종료 날짜 (일요일) |
    | dailyExpenses | array | 요일별 지출 리스트 |
    | dailyExpenses[].dayOfWeek | string | 요일 (`MON`~`SUN`) |
    | dailyExpenses[].date | string | 날짜 |
    | dailyExpenses[].amount | number | 해당 날짜 지출 금액 |
    | weeklyTotalExpense | number | 주간 총 지출 |
    | maxExpenseDay | string | 최대 소비 요일 |
    | lastWeekComparison.lastWeekTotalExpense | number | 지난주 총 지출 |
    | lastWeekComparison.changeAmount | number | 지출 변화 금액 |
    | lastWeekComparison.changeRate | number | 지출 변화율 (%) |
    | topCategory.categoryId | number | 지출 1위 카테고리 ID |
    | topCategory.categoryName | string | 지출 1위 카테고리명 |
    | topCategory.amount | number | 지출 1위 카테고리 금액 |
    | peerRanking.percentile | number | 또래 대비 소비 순위 상위 (%) |
    | categoryExpenses | array | 카테고리별 지출 리스트 |
    | categoryExpenses[].categoryId | number | 카테고리 ID |
    | categoryExpenses[].categoryName | string | 카테고리명 |
    | categoryExpenses[].amount | number | 해당 카테고리 지출 금액 |
    | categoryExpenses[].percentage | number | 전체 대비 비율 (%) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "날짜 형식이 올바르지 않습니다.",
      "path": "/api/v1/reports/weekly"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/reports/weekly"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | date 파라미터가 누락되었거나 형식이 올바르지 않은 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ---
    
    # 월간 리포트 조회
    
    Method: GET
    URI patterns: /api/v1/reports/monthly
    완료: No
    
    # 설명 및 권한
    
    지정한 월의 월간 소비 리포트를 조회한다. 예산 소진율, 카테고리별 지출 비율(Top 3), 가장 큰 지출 Top 3 등을 포함한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/reports/monthly`
    
    ex) `/api/v1/reports/monthly?month=2026-06`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | month | string | O | 조회할 월 (`YYYY-MM` 형식) |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "targetMonth": "2026-06",
        "totalExpenseBudget": 800000,
        "totalSpent": 650000,
        "remainingBudget": 150000,
        "usageRate": 81.3,
        "topCategories": [
          { "rank": 1, "categoryId": 1, "categoryName": "식비", "amount": 280000, "percentage": 43.1 },
          { "rank": 2, "categoryId": 3, "categoryName": "쇼핑", "amount": 150000, "percentage": 23.1 },
          { "rank": 3, "categoryId": 2, "categoryName": "교통", "amount": 95000, "percentage": 14.6 }
        ],
        "topExpenses": [
          { "expenseId": 50, "placeName": "나이키", "amount": 89000, "categoryName": "쇼핑", "expenseDate": "2026-06-15T14:00:00" },
          { "expenseId": 33, "placeName": "배달의민족", "amount": 45000, "categoryName": "식비", "expenseDate": "2026-06-08T19:30:00" },
          { "expenseId": 67, "placeName": "CGV", "amount": 38000, "categoryName": "문화", "expenseDate": "2026-06-22T18:00:00" }
        ],
        "lastMonthComparison": {
          "lastMonthTotalSpent": 680000,
          "changeAmount": -30000
        }
      },
      "message": "월간 리포트 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | targetMonth | string | 대상 월 |
    | totalExpenseBudget | number | 목표 지출 예산 총액 |
    | totalSpent | number | 실제 총 지출 금액 |
    | remainingBudget | number | 남은 예산 |
    | usageRate | number | 예산 소진율 (%) |
    | topCategories | array | 카테고리별 지출 비율 Top 3 |
    | topCategories[].rank | number | 순위 |
    | topCategories[].categoryId | number | 카테고리 ID |
    | topCategories[].categoryName | string | 카테고리명 |
    | topCategories[].amount | number | 해당 카테고리 지출 금액 |
    | topCategories[].percentage | number | 전체 대비 비율 (%) |
    | topExpenses | array | 가장 큰 지출 Top 3 |
    | topExpenses[].expenseId | number | 지출 기록 ID |
    | topExpenses[].placeName | string | 사용처 |
    | topExpenses[].amount | number | 지출 금액 |
    | topExpenses[].categoryName | string | 카테고리명 |
    | topExpenses[].expenseDate | string | 지출 날짜 |
    | lastMonthComparison.lastMonthTotalSpent | number | 지난달 총 지출 |
    | lastMonthComparison.changeAmount | number | 지출 변화 금액 (음수: 절약) |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "월 형식이 올바르지 않습니다.",
      "path": "/api/v1/reports/monthly"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/reports/monthly"
    }
    ```
    
    ## HTTP Status code: `404 Not Found`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 404,
      "error": "Not Found",
      "code": "MONTHLY_FINANCE_NOT_FOUND",
      "message": "해당 월의 예산 설정 정보가 존재하지 않습니다.",
      "path": "/api/v1/reports/monthly"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | month 파라미터가 누락되었거나 형식이 올바르지 않은 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    | 404 Not Found | MONTHLY_FINANCE_NOT_FOUND | 해당 월의 월간 재무 설정 데이터가 없는 경우 |
    
    ---
    
    # 연간 리포트 조회
    
    Method: GET
    URI patterns: /api/v1/reports/annual
    완료: No
    
    # 설명 및 권한
    
    지정한 연도(1월~12월)의 연간 소비 리포트를 조회한다. 월별 지출 추이, 가장 많이 쓴 달과 절약한 달, 총 절약 금액 등을 포함한다.
    
    로그인한 사용자만 요청할 수 있다.
    
    # URL
    
    `GET /api/v1/reports/annual`
    
    ex) `/api/v1/reports/annual?year=2025`
    
    # 필요 정보
    
    ## Authorization Header
    
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
    ```
    
    | 구분 | name | type | 필수 | 설명 |
    | --- | --- | --- | --- | --- |
    | Header | Authorization | string | O | `Bearer {accessToken}` 형식의 DELTA Access Token |
    
    ## Request Parameter
    
    | name | type | 필수 | 설명 |
    | --- | --- | --- | --- |
    | year | number | O | 조회할 연도 (예: 2025) |
    
    ## Request Body
    
    ```json
    없음
    ```
    
    # ✅ Success Response
    
    ## HTTP Status code: `200 OK`
    
    ### Content
    
    ```json
    {
      "success": true,
      "data": {
        "year": 2025,
        "monthlyExpenses": [
          { "month": "2025-01", "totalSpent": 620000, "totalBudget": 700000 },
          { "month": "2025-02", "totalSpent": 580000, "totalBudget": 700000 },
          { "month": "2025-03", "totalSpent": 750000, "totalBudget": 700000 },
          { "month": "2025-04", "totalSpent": 490000, "totalBudget": 700000 },
          { "month": "2025-05", "totalSpent": 680000, "totalBudget": 700000 },
          { "month": "2025-06", "totalSpent": 650000, "totalBudget": 800000 },
          { "month": "2025-07", "totalSpent": 720000, "totalBudget": 800000 },
          { "month": "2025-08", "totalSpent": 550000, "totalBudget": 800000 },
          { "month": "2025-09", "totalSpent": 610000, "totalBudget": 750000 },
          { "month": "2025-10", "totalSpent": 690000, "totalBudget": 750000 },
          { "month": "2025-11", "totalSpent": 830000, "totalBudget": 750000 },
          { "month": "2025-12", "totalSpent": 900000, "totalBudget": 800000 }
        ],
        "annualSummary": {
          "totalSpent": 8070000,
          "totalBudget": 8950000,
          "totalSaved": 880000,
          "highestSpendingMonth": "2025-12",
          "lowestSpendingMonth": "2025-04"
        },
        "categorySavings": [
          { "categoryId": 1, "categoryName": "식비", "savedAmount": 350000 },
          { "categoryId": 2, "categoryName": "교통", "savedAmount": 180000 },
          { "categoryId": 3, "categoryName": "쇼핑", "savedAmount": 200000 },
          { "categoryId": 4, "categoryName": "문화", "savedAmount": 150000 }
        ]
      },
      "message": "연간 리포트 조회 성공"
    }
    ```
    
    | name | type | 설명 |
    | --- | --- | --- |
    | year | number | 대상 연도 |
    | monthlyExpenses | array | 월별 지출 리스트 |
    | monthlyExpenses[].month | string | 대상 월 (`YYYY-MM`) |
    | monthlyExpenses[].totalSpent | number | 해당 월 실제 지출 |
    | monthlyExpenses[].totalBudget | number | 해당 월 목표 예산 |
    | annualSummary.totalSpent | number | 연간 총 지출 |
    | annualSummary.totalBudget | number | 연간 총 목표 예산 |
    | annualSummary.totalSaved | number | 총 절약 금액 (= totalBudget - totalSpent) |
    | annualSummary.highestSpendingMonth | string | 가장 많이 쓴 달 |
    | annualSummary.lowestSpendingMonth | string | 가장 절약한 달 |
    | categorySavings | array | 카테고리별 절약 금액 리스트 |
    | categorySavings[].categoryId | number | 카테고리 ID |
    | categorySavings[].categoryName | string | 카테고리명 |
    | categorySavings[].savedAmount | number | 해당 카테고리 절약 금액 |
    
    # ❌ Fail Response
    
    ## HTTP Status code: `400 Bad Request`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 400,
      "error": "Bad Request",
      "code": "INVALID_INPUT_VALUE",
      "message": "연도 형식이 올바르지 않습니다.",
      "path": "/api/v1/reports/annual"
    }
    ```
    
    ## HTTP Status code: `401 Unauthorized`
    
    ### Content
    
    ```json
    {
      "timestamp": "2026-07-15T16:20:00.000+09:00",
      "status": 401,
      "error": "Unauthorized",
      "code": "INVALID_ACCESS_TOKEN",
      "message": "유효하지 않거나 만료된 Access Token입니다.",
      "path": "/api/v1/reports/annual"
    }
    ```
    
    | HTTP Status | code | 발생 조건 |
    | --- | --- | --- |
    | 400 Bad Request | INVALID_INPUT_VALUE | year 파라미터가 누락되었거나 형식이 올바르지 않은 경우 |
    | 401 Unauthorized | INVALID_ACCESS_TOKEN | Access Token이 없거나, 만료되었거나, 서명이 올바르지 않은 경우 |
    
    ```