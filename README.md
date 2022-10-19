## Источники и ссылки

* [Иван Пономарёв, КУРС - Kafka Streams API: шаг за рамки Hello World. Part 1](https://www.youtube.com/watch?v=pipM6bwQjoM)
* [Иван Пономарёв, КУРС - Kafka Streams API: шаг за рамки Hello World. Part 2](https://www.youtube.com/watch?v=PqQax9zur9I)
* [Stephane Marek, Create a KSQL Stream Tutorial](https://www.youtube.com/watch?v=3gj_CHlVkNE)
* [Виктор Гамов, Барух Садогурский — Боремся с Russian Hackers с помощью Kafka Streams и Firehose API](https://www.youtube.com/watch?v=ZH3AlesuSpw)


* [ksqlDB Quickstart](https://ksqldb.io/quickstart.html)
* [ksqlDB Quickstart by Confluent](https://docs.confluent.io/5.0.4/ksql/docs/quickstart.html)


* [Kafka Streams Guide](https://kafka.apache.org/21/documentation/streams/developer-guide/config-streams.html)
* [Kafka Stream DSL](https://kafka.apache.org/20/documentation/streams/developer-guide/dsl-api.html)


* [Визуализатор топологии стримов](https://zz85.github.io/kafka-streams-viz/)

## Основы

**KSQL** — это механизм потокового SQL для Apache Kafka®.
С помощью KSQL вы можете писать приложения для потоковой передачи в реальном времени,
используя язык запросов, подобный SQL.

**Kafka Streams** — это библиотека Apache Kafka® для написания потоковых приложений и микросервисов на Java и Scala.


<img src="https://docs.confluent.io/5.0.4/_images/ksql-kafka-streams-core-kafka-stack.png" width="800px" alt="ksql-kafka-streams-core-kafka-stack"/>

## Пример кода

**kSQL**

```ksql
CREATE STREAM fraudulent_payments AS
SELECT fraudProbability(data) FROM payments
WHERE fraudProbability(data) > 0.8;
```

**Scala**

```scala
// Example fraud-detection logic using the Kafka Streams API.
object FraudFilteringApplication extends App {

    val builder: StreamsBuilder = new StreamsBuilder()
    
    val fraudulentPayments: KStream[String, Payment] 
        = builder
            .stream[String, Payment]("payments-kafka-topic")
            .filter((_ ,payment) => payment.fraudProbability > 0.8)
                                                            
    fraudulentPayments.to("fraudulent-payments-topic")

    val config = new java.util.Properties
    
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "fraud-filtering-app")
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker1:9092")

    val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
    
    streams.start()
}
```

## Отличия подхода

**KSQL**

Вы пишете запросы KSQL в интерактивном режиме и просматриваете результаты в режиме реального времени
либо в интерфейсе командной строки KSQL, либо в Confluent Control Center.
Вы можете сохранить файл .sql и развернуть его в рабочей среде как «headless» приложение,
которое работает без графического интерфейса, интерфейса командной строки или интерфейса REST на серверах KSQL.

**Kafka Streams**

Вы пишете код на Java или Scala, перекомпилируете, запускаете и тестируете приложение в среде IDE,
например IntelliJ. Вы развертываете приложение в рабочей среде в виде JAR-файла,
который запускается в кластере Kafka.

**Начните с KSQL, когда…**

* Новое в потоковом вещании и Kafka

* Чтобы ускорить и расширить внедрение и ценность Kafka в вашей организации

* Предпочитаете интерактивный опыт с пользовательским интерфейсом и интерфейсом командной строки

* Предпочитайте SQL написанию кода на Java или Scala

* Варианты использования включают обогащение данных; объединение источников данных;
  фильтрация, преобразование и маскирование данных; выявление аномальных событий

* Вариант использования естественным образом выражается с помощью SQL
  с дополнительной помощью пользовательских функций.

* Хотите мощь Kafka Streams, но не используете JVM:
  используйте KSQL REST API из Python, Go, C#, JavaScript, shell

**Начните с Kafka Streams, когда…**

* Предпочитаете написание и развертывание приложений JVM, таких как Java и Scala;
  например, благодаря навыкам работы с людьми, технической среде

* Вариант использования не выражается естественным образом через SQL, например, конечные автоматы.

* Создание микросервисов

* Необходимо интегрировать с внешними службами
  или использовать сторонние библиотеки (но могут помочь пользовательские функции KSQL)

* Для настройки или точной настройки варианта использования,
  например, с API Kafka Streams Processor: настраиваемые варианты
  соединения или вероятностный подсчет в очень больших масштабах с помощью Count-Min Sketch.

* Нужно запрашиваемое состояние, которое KSQL не поддерживает

## KSQL Architecture

#### KSQL Components**

* **KSQL engine** – обрабатывает операторы и запросы KSQL.
* **REST interface** – позволяет клиенту получить доступ к движку.
* **KSQL CLI** – консоль, которая предоставляет интерфейс командной строки (CLI) для движка.
* **_KSQL UI_** – _позволяет разрабатывать приложения KSQL в Confluent Control Center._

<img src="https://docs.confluent.io/5.0.4/_images/ksql-architecture-and-components.png" width="800px" alt="ksql-architecture-and-components"/>

## KSQL Language Elements

#### Data Definition Language (DDL) Statements

Глаголы императива, которые определяют метаданные на сервере KSQL путем добавления,
изменения или удаления потоков и таблиц. Операторы языка определения данных изменяют
только метаданные и не работают с данными.
Вы можете использовать эти операторы с декларативными операторами DML.

* `CREATE STREAM`
* `CREATE TABLE`
* `DROP STREAM`
* `DROP TABLE`
* `CREATE STREAM AS SELECT (CSAS)`
* `CREATE TABLE AS SELECT (CTAS)`

#### Data Manipulation Language (DML)

Декларативные глаголы, которые считывают и изменяют данные в потоках и таблицах KSQL.
Операторы языка манипулирования данными изменяют только данные и не изменяют метаданные.
Механизм KSQL компилирует операторы DML в приложения Kafka Streams,
которые работают в кластере Kafka, как и любое другое приложение Kafka Streams.

* `SELECT`
* `INSERT INTO`
* `CREATE STREAM AS SELECT (CSAS)`
* `CREATE TABLE AS SELECT (CTAS)`

Операторы **CSAS** и **CTAS** относятся к обеим категориям,
поскольку они выполняют как изменение метаданных,
например добавление потока, так и манипулирование данными,
создавая производные существующие записи/

## Режимы развертывания KSQL

#### Interactive Deployment

Используйте интерактивный режим для разработки приложений KSQL.
Когда вы развертываете сервер KSQL в интерактивном режиме,
интерфейс REST доступен для подключения KSQL CLI.

<img src="https://docs.confluent.io/5.0.4/_images/ksql-client-server-interactive-mode.png" width="800px" alt="ksql-client-server-interactive-mode"/>

**В интерактивном режиме вы можете:**

* Пишите заявления и запросы на лету
* Запустите любое количество узлов сервера:`<path-to-confluent>/bin/ksql-server-start`
* Запустите один или несколько интерфейсов командной строки или клиентов
  REST и направьте их на сервер: `<path-to-confluent>/bin/ksql https://<ksql-server-ip-address>:8090`

#### Headless Deployment

Используйте автономный режим для развертывания приложения **KSQL**
в производственной среде. Когда вы развертываете сервер **KSQL** в
автономном режиме, интерфейс **REST** недоступен, поэтому вы назначаете
рабочие нагрузки серверам **KSQL** с помощью файла **SQL**. Файл **SQL** содержит операторы и запросы **KSQL**,
определяющие ваше приложение. Безголовый режим идеально подходит
для потокового развертывания приложений **ETL**.

<img src="https://docs.confluent.io/5.0.4/_images/ksql-standalone-headless.png" width="800px" alt="ksql-standalone-headless"/>

**В headless режиме вы можете:**

* Запуск любого количества узлов сервера
* Передайте файл **SQL** с операторами **KSQL** для
  выполнения: `<path-to-confluent>bin/ksql-node query-file=path/to/myquery.sql`
* Управляйте версиями ваших запросов и преобразований как кода
* Обеспечьте изоляцию ресурсов
* Оставьте управление ресурсами выделенным системам, таким как **Kubernetes**.

### Жизненный цикл запроса KSQL

Чтобы создать потоковое приложение с **KSQL**, вы пишете операторы и запросы на **KSQL**.
Каждый оператор и запрос имеет жизненный цикл со следующими этапами:

1. Вы регистрируете поток или таблицу **KSQL** из существующего топика Kafka
   с помощью инструкции **DDL**, например `CREATE STREAM <my-stream> WITH <topic-name>`

2. Вы выражаете свое приложение с помощью оператора **KSQL**, например `CREATE TABLE AS SELECT FROM <my-stream>`

3. **KSQL** преобразует ваше выражение в абстрактное синтаксическое дерево (**AST**)

4. **KSQL** использует **AST(Abstract Syntax Tree)** и создает логический план для вашего заявления

5. **KSQL** использует логический план и создает физический план для вашего оператора

6. **KSQL** генерирует и запускает приложение Kafka Streams.

7. Вы управляете приложением как **STREAM** или **TABLE** с соответствующим постоянным запросом.

#### KSQL создает логический план

Механизм KSQL создает логический план запроса с помощью **AST(Abstract Syntax Tree)**.

```ksql
CREATE TABLE possible_fraud AS
SELECT card_number, count(*)
FROM authorization_attempts
    WINDOW TUMBLING
    (SIZE 5 SECONDS)
    WHERE region = ‘west’            
  GROUP BY card_number             
  HAVING count(*) > 3;
```

<img src="https://docs.confluent.io/5.0.4/_images/ksql-statement-logical-plan.gif" width="800px" alt="ksql-standalone-headless"/>

Порядок создания плана:
1. Определить источник — узел **FROM**
2. Применить фильтр – предложение **WHERE**
3. Применить агрегацию – **GROUP BY**
4. Спроецыровать — WINDOW
5. Применить фильтр постагрегации – **HAVING**, применяется к результату **GROUP BY**
6. Спроецыровать – на результат