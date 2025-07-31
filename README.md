# Employee Database Application
## Описание
Консольное приложение для управления базой данных сотрудников с поддержкой PostgreSQL. Приложение предоставляет следующие функции:
- Создание таблицы сотрудников
- Добавление записей о сотрудниках
- Просмотр и сортировка записей
- Массовая генерация тестовых данных
- Тестирование производительности запросов
- Оптимизация базы данных

## Требования
1. Java JDK 11 или новее
2. PostgreSQL 14+
3. Maven (для сборки)

## Установка
1. Клонируйте репозиторий:

```bash
git clone https://github.com/your-repo/employee-db-app.git
cd employee-db-app
```
2. Настройте базу данных:

```bash
createdb employee_db
psql -c "CREATE USER employee_user WITH PASSWORD 'strong_password';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE employee_db TO employee_user;"
```
3. Настройте конфигурацию: 
Отредактируйте файл src/main/resources/config.properties:

```properties
db.url=jdbc:postgresql://localhost:5432/employee_db
db.user=employee_user
db.password=strong_password
```
## Сборка и запуск
### Сборка с Maven:
```bash
mvn clean package
```
### Ручная сборка:
```bash
## Создание структуры каталогов
mkdir -p target/classes
mkdir -p target/lib

## Компиляция
javac -d target/classes -cp ".;lib/postgresql-42.5.6.jar" src/main/java/*.java

##  Создание JAR
jar cfe target/employee-app.jar EmployeeApp -C target/classes . -C src/main/resources .

## Копирование драйвера
cp lib/postgresql-42.5.6.jar target/
```
### Запуск приложения:
```bash
java -cp "target/employee-app.jar;target/postgresql-42.5.6.jar" EmployeeApp <режим> [аргументы]
```
### Режимы работы
| Режим | 	Команда                                                               | Описание                                            |
|-------|------------------------------------------------------------------------|-----------------------------------------------------|
| 1	    | java -jar employee-app.jar 1	                                          | Создание таблицы сотрудников                        |
| 2	    | java -jar employee-app.jar 2 "Ivanov Petr Sergeevich" 1990-05-15 Male	 | Добавление сотрудника                               |
| 3	    | java -jar employee-app.jar 3	                                          | Просмотр уникальных сотрудников (сортировка по ФИО) |
| 4	    | java -jar employee-app.jar 4	                                          | Генерация тестовых данных (1M+100 записей)          |
| 5	    | java -jar employee-app.jar 5	                                          | Тест производительности без оптимизации             |
| 6	    | java -jar employee-app.jar 6	                                          | Тест производительности с оптимизацией              |

## Структура проекта
```text
employee-db-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── EmployeeApp.java         # Главный класс
│   │   │   ├── Employee.java            # Модель сотрудника
│   │   │   ├── DatabaseManager.java     # Работа с БД
│   │   │   └── PerformanceTester.java   # Тесты производительности
│   │   └── resources/
│   │       └── config.properties        # Конфигурация БД
│   └── test/                           # Тесты (JUnit)
├── lib/
│   └── postgresql-42.5.6.jar           # Драйвер PostgreSQL
├── target/                             # Сгенерированные файлы
├── pom.xml                             # Конфигурация Maven
└── README.md
```  