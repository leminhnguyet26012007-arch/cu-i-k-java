# He thong Quan ly Sinh vien - JavaFX Desktop MVC

Day la ung dung quan ly sinh vien viet bang JavaFX theo mo hinh MVC. Du an da duoc chuyen sang ung dung desktop, khong con giao dien web, Spring MVC Controller, Thymeleaf template hay HTTP browser UI.

## Cong nghe

- Java 17+
- JavaFX
- Maven
- JDBC
- H2 local database mac dinh
- PostgreSQL/Supabase tuy chon qua `config.properties`
- BCrypt cho mat khau
- AES cho thong tin nhay cam nhu email/SDT

## Mo hinh MVC

```text
src/main/java/com/example/dean12/model
```

Chua cac lop model/entity: `User`, `SinhVien`, `GiangVien`, `MonHoc`, `LopHocPhan`, `DangKyHoc`, `Diem`, `Attendance`, `Feedback`, `ThongBao`, `HocPhi`.

```text
src/main/java/com/example/dean12/desktop/view
```

Chua cac man hinh JavaFX:

- `LoginSceneFactory`
- `AdminScenes`, `AdminAdvancedScenes`
- `TeacherScenes`, `TeacherAdvancedScenes`
- `StudentScenes`, `StudentAdvancedScenes`

```text
src/main/java/com/example/dean12/desktop/controller
```

Chua controller desktop:

- `LoginController`: xu ly dang nhap
- `AdminController`: quan ly sinh vien, mon hoc, lop hoc phan, tai khoan, thong bao, cau hinh
- `TeacherController`: lop phu trach, diem danh, nhap diem, khoa diem, ho so giang vien
- `StudentController`: thoi khoa bieu, diem, hoc phi, dang ky hoc phan, phan hoi, ho so sinh vien
- `SceneNavigator`: dieu huong man hinh JavaFX va giu thong tin user hien tai

```text
src/main/java/com/example/dean12/desktop/data/DesktopDao.java
```

Tang DAO cho desktop, noi controller voi tang du lieu.

```text
src/main/java/com/example/dean12/desktop/network/ServerDao.java
```

Xu ly JDBC, tao schema, seed du lieu mau va thao tac database.

## Chuc nang chinh

### Admin

- Quan ly sinh vien
- Quan ly mon hoc
- Quan ly lop hoc phan
- Quan ly tai khoan
- Khoa/mo khoa tai khoan
- Tao thong bao
- Cau hinh hoc ky, nam hoc, hoc phi/tin chi
- Xuat/nhap danh sach sinh vien bang XML
- Xem thong ke tong quan

### Giang vien

- Xem lop hoc phan phu trach
- Diem danh sinh vien
- Nhap diem qua bang diem
- Sua diem bang nut `Sua` tung dong khi bang diem chua khoa
- Khoa bang diem
- Gui thong bao theo lop
- Upload thong tin tai lieu
- Cap nhat ho so giang vien

### Sinh vien

- Xem tong quan hoc tap
- Xem thoi khoa bieu
- Xem bang diem
- Dang ky hoc phan
- Xem/thanh toan hoc phi
- Gui phan hoi/don tu
- Xem thong bao
- Cap nhat ho so ca nhan

## Cach chay ung dung desktop

Chay file:

```text
RUN_DESKTOP.bat
```

Hoac trong IntelliJ IDEA chay:

```text
src/main/java/com/example/dean12/Main.java
```

Ung dung desktop se tu tao schema, nap du lieu mau neu can va dang nhap truc tiep bang database local mac dinh.

## Tai khoan mau

```text
admin / 123
gv01  / 123
sv01  / 123
```

Co san them:

```text
gv01 -> gv05
sv01 -> sv60
```

Mat khau mac dinh: `123`.

## Kiem tra dang nhap

Chay:

```powershell
mvnw -Pverify-login compile exec:java
```

Neu thanh cong se thay:

```text
admin OK (ADMIN)
gv01 OK (TEACHER)
sv01 OK (STUDENT)
```

## Cau hinh database

Mac dinh ung dung dung H2 local:

```properties
database.url=jdbc:h2:file:./database/school_db;MODE=PostgreSQL
database.username=sa
database.password=
```

File cau hinh:

```text
config.properties
src/main/resources/config.properties
```

Neu muon dung PostgreSQL/Supabase:

```properties
database.url=jdbc:postgresql://host:5432/postgres?sslmode=require
database.username=postgres
database.password=your_password
```

## TCP server tuy chon

Ung dung desktop hien khong bat buoc chay `ServerMain`.

`ServerMain` chi dung khi can test TCP socket rieng:

```text
START_SERVER.bat
```

Mac dinh TCP server dung port:

```properties
server.port=9000
```

Neu gap loi:

```text
Address already in use: bind
```

thi port `9000` dang bi tien trinh khac giu. Co the dung PowerShell:

```powershell
Get-NetTCPConnection -LocalPort 9000 | Select-Object LocalAddress,LocalPort,State,OwningProcess
Stop-Process -Id <PID>
```

Hoac doi port trong `config.properties`:

```properties
server.port=9001
```

## Script

```text
RUN_DESKTOP.bat      Chay JavaFX desktop client
START_SERVER.bat     Chay TCP server tuy chon
SEED_SUPABASE.bat    Nap du lieu mau vao database dang cau hinh
VERIFY_SUPABASE.bat  Kiem tra so dong database dang cau hinh
CLEAN_BOM.bat        Xu ly file encoding/BOM neu can
```

## Build

```powershell
mvnw clean compile
```

Hoac:

```powershell
mvnw clean package
```

Thu muc `target/` la output cua Maven va khong can day len GitHub.

## Ghi chu

- Khong con thu muc `templates`.
- Khong con controller Spring Web trong package `controller`.
- Khong con `application.properties` cho Spring Boot web app.
- File database local H2 trong `database/*.mv.db` duoc ignore, khong day len GitHub.
