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

### Kafka 실행
- 카프카 실행
  - ./bin/kafka-server-start.sh ./config/kraft/server.properties
  - nohup ./bin/kafka-server-start.sh ./config/kraft/server.properties > /dev/null 2>&1 &

### Topic Console 명령어
- 토픽 리스트 확인
  - ./bin/kafka-topics.sh --list --bootstrap-server localhost:9092
- 토픽 생성
  - ./bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic 토픽명
- 토픽 상세보기
  - ./bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic test
- 토픽 데이터 확인 
  - ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
