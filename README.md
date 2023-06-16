# Covid19CenterApp

코로나19 예방접종센터 지도 서비스

[공공데이터활용지원센터\_코로나19 예방접종센터 조회서비스](https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15077586) API를 사용합니다.

### 기술 스택

- Jetpack Compose
- Ktor Client
- MVVM *(No AAC)*
- Naver Map
- Hilt
- DataStore
- Coroutines
- Kotest (test framework)

### 주요 기능

- 데이터 [prefetch](https://en.wikipedia.org/wiki/Prefetching)
  - 2초에 걸쳐 100% 로딩
  - prefetch가 완료되지 않았다면 80% 로딩에서 대기
  - 저장이 완료되면 0.4초에 걸쳐 100% 로딩
- 네이버 지도에 불러온 데이터 표시
  - 데이터 타입에 따라 마커 색상 구분 
  - 마커 클릭시 카메라 이동 및 주요 데이터 표시
- 현재 위치 버튼 (라이브러리 제공 기능이 아닌 직접 구현)
  - 버튼 클릭 시 현재 위치로 이동

### 코드 스타일

오랜 시간 개발 경험에 의해 본인이 가장 편하게 느낄 수 있는 코드 스타일을 사용하였습니다. 

- 들여쓰기 2칸
- trailing comma 필수 사용
- declaration new-line
