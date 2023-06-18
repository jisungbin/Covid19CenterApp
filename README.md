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
 
### 아키텍처 및 디자인 패턴

- Clean Architecture: 클아를 도입하기엔 프로젝트가 매우 작아, 클아를 도입하는 비용에 비해 얻는 이점이 아주 사소하다고 판단하여 사용하지 않음.
- Repository Pattern: 레포지토리를 도입하기엔 비즈니스 레이어가 매우 작고, 비즈니스 usecase도 하나 뿐이라 레포지토리를 도입하는 비용에 비해 얻는 이점이 아주 사소하다고 판단하여 사용하지 않음.
- AAC ViewModel: 본 앱을 컴포즈 100%로 만들면서 AAC VM의 장점을 모두 대체할 수 있는 방안이 제공됨. 해당 방안을 적극 사용하고, VM에 AAC 개념을 지우면 더 Testable하고 Reusable한 코드가 되기에 AAC VM을 사용하지 않음.
- AAC가 아닌 MVVM은 android를 몰라야 하고, 앱 내에 비즈니스 로직이 매우 적어(api request, datastore fetch) VM에는 api request 코드만 넣고, datastore fetch는 액티비티에서 진행함.

### 빌드 및 다운로드

- 빌드: 본 앱은 안드로이드 스튜디오의 카나리 버전을 사용합니다. 만약 AGP 호환성 문제가 발생한다면 version catalog에서 `gradle-android` 버전을 조정해 주세요.
- 다운로드: 본 앱의 release build는 GitHub Release 페이지에서 다운로드하실 수 있습니다.

### 코드 스타일

오랜 시간 개발 경험에 의해 본인이 가장 편하게 느낄 수 있는 코드 스타일을 사용하였습니다. 

- 들여쓰기 2칸
- trailing comma 필수 사용
- declaration new-line
