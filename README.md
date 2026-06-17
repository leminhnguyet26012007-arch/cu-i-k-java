# He thong Quan ly Sinh vien - Java Desktop Client/Server

Ung dung dung mo hinh Client/Server tach biet:

- `ServerMain`: chay server Spring Boot + TCP Socket, ket noi Supabase va thao tac database.
- `Main`: chay client JavaFX desktop, chi hien thi UI va gui request den server.

Client khong ket noi truc tiep database.

## Cau truc chinh

Client:
- UI Layer: `src/main/java/com/example/dean12/desktop`
- Client Service Layer: `DesktopDao`
- DTO/Model: `src/main/java/com/example/dean12/model`

Server:
- Socket/Handler: `TcpSocketServer`
- Business dispatch: `handleRequest`
- DAO/Repository: `ServerDao`, cac repository Spring
- Security: BCrypt cho password, AES cho email/SDT
- Database: Supabase PostgreSQL

## Cach chay trong IntelliJ

Chay dung thu tu sau:

1. Mo file:

```text
src/main/java/com/example/dean12/ServerMain.java
```

Bam nut Run mau xanh canh `main`.

Khi server chay dung, console se co cac dong tuong tu:

```text
[TCP Server] Server listening on port 9000
Web: http://localhost:8081/login
```

Chi can thay dong `[TCP Server] Server listening on port 9000` la co the chay client `Main`. Khong can doi seed du lieu moi lan mo server.

2. Khong tat server. Tiep tuc mo file:

```text
src/main/java/com/example/dean12/Main.java
```

Bam nut Run mau xanh canh `main`.

File `Main` se mo giao dien JavaFX desktop.

## Dang nhap

Tai khoan mau:

```text
admin / 123
gv01 / 123
sv01 / 123
```

## Giai thich cac cong

- `http://localhost:8081/login`: giao dien web Spring Boot. Mo bang browser duoc.
- `localhost:9000`: TCP Socket server cho JavaFX desktop. Day khong phai trang login web.

Neu mo `http://localhost:9000/` bang browser, no chi hien trang trang thai server. Desktop client moi la noi dang nhap qua port 9000.

## Cau hinh Supabase

File cau hinh:

```text
config.properties
src/main/resources/config.properties
src/main/resources/application.properties
```

Database hien dang tro den Supabase project:

```text
upagdhbrgkkbkxadzsao
```

Du lieu mau da nap:

- 66 users
- 5 giang vien
- 60 sinh vien
- 12 mon hoc
- 15 lop hoc phan
- 300 dang ky hoc
- 300 diem

## Script tuy chon

Neu khong muon bam Run trong IntelliJ, co the dung:

```text
START_SERVER.bat    chay server
RUN_DESKTOP.bat     chay client
SEED_SUPABASE.bat   nap lai du lieu mau khi can, khong chay moi lan start server
VERIFY_SUPABASE.bat kiem tra so dong tren Supabase
```

## Build JAR

```powershell
cmd /c mvnw.cmd clean -DskipTests package
```

File build:

```text
target/dean12-1.0.0.jar
```

`target` la thu muc Maven tu sinh khi build/chay. Co the xoa, Maven se tao lai.
