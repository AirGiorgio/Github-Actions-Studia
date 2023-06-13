
zbudowanie aplikacji:
```shell
docker build -t app1:latest .
```

uruchomienie aplikacji:
```shell
docker run -d --name app1 -p 8080:8080 app1:latest
```

logi uruchomionej aplikacji:
```shell
docker logs app1
```

sprawdzenie warstw w obrazie:
```shell
docker history app1:latest
```