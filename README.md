# Kafka Kraft 테스트

## Kraft 설치

### Kafka Cluster Id 설정
- cluster id 생성
  - ./bin/kafka-storage.sh random-uuid
- cluster id 값을 통해 log directory 초기화
  - default 디렉토리는 /tmp/kraft-combined-logs
  - log 디렉토리에는 offset 과 메타정보들 존재.
  - 카프카 클러스터 설정 변경 또는 노드 추가시 이 디렉토리를 초기화 고려
  - ./bin/kafka-storage.sh format -t UUID -c ./config/kraft/server.properties
- 메모
  - WSL2 에 카프카 설치시 listener ip 주소는 WSL2 의 IP 입력 (localhost 입력 x)
  - 만약 안된다면? 
    - 윈도우 방화벽 오픈 (방화벽 -> 고급설정 -> 인바운드규칙 -> 포트추가)
    - 그래도 안되면 아래 참고
      - https://codeac.tistory.com/118
      - https://github.com/microsoft/WSL/issues/4150
      - https://github.com/microsoft/WSL/issues/5728


### Kafka 실행
- 카프카 실행
  - ./bin/kafka-server-start.sh ./config/kraft/server.properties
  - 백그라운드실행: nohup ./bin/kafka-server-start.sh ./config/kraft/server.properties > /dev/null 2>&1 &


### Topic Console 명령어
- 토픽 리스트 확인
  - ./bin/kafka-topics.sh --list --bootstrap-server 172.18.189.24:9092
- 토픽 생성
  - ./bin/kafka-topics.sh --create --bootstrap-server 172.18.189.24:9092 --replication-factor 1 --partitions 1 --topic 토픽이름
- 토픽 상세보기
  - ./bin/kafka-topics.sh --describe --bootstrap-server 172.18.189.24:9092 --topic 토픽이름
- 토픽 데이터 확인 
  - ./bin/kafka-console-consumer.sh --bootstrap-server 172.18.189.24:9092 --topic 토픽이름 --from-beginning
- 컨슈머 그룹 조회
  - ./bin/kafka-consumer-groups.sh --bootstrap-server 10.1.7.161:9092 --list
- 컨슈머 그룹 체크
  - ./bin/kafka-consumer-groups.sh --bootstrap-server 10.1.7.161:9092 --group 컨슈머그룹이름 --describe
    ![image](https://user-images.githubusercontent.com/10750614/163079692-9d57f4e1-0c17-45f0-9bd0-5f4bf61d7a83.png)
- 컨슈머 그룹 파티션 수 체크
  - ./bin/kafka-consumer-groups.sh --bootstrap-server 10.1.7.161:9092 --group 컨슈머그룹이름 --describe -- members


# Redis 

참고 : https://jdragon.oopy.io/6e084b7f-c77c-4c80-bcda-edc499e603e7