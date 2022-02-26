- Проверка статуса миграции
```
mvn -Pdev liquibase:status -Dliquibase.verbose
```
- Применение миграции
```
mvn -Pdev liquibase:update  
```
- Посмотреть SQL запросы, которые выполнятся при миграции
```
mvn -Pdev liquibase:updateSQL
```
- Откатить последний changeSet
```
mvn -Pdev liquibase:rollback -Dliquibase.rollbackCount=1
```

### Примеры работы с командной строкой

- Установить liquibase:
https://docs.liquibase.com/concepts/installation/installation-linux-unix-mac.html

- В папку lib установленного liquibase установить драйвер базы данных.

```
liquibase --defaultsFile=src/main/resources/db/dev/liquibase.properties --changeLogFile=src/main/resources/db/changelog/V0.1.xml status --verbose
```

```
liquibase --defaultsFile=src/main/resources/db/dev/liquibase.properties --changeLogFile=src/main/resources/db/changelog/V0.1.xml updateSQL
```