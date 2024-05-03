# TugasPBO_FODOL (FOOD ONLINE)

#### Nama: I Gusti Agung Ngurah Lucien Yudistira Purnawarman
#### NIM: 2305551152 
#### Kelas: PBO kelas E

## Deskripsi Program


Program FODOL(FOOD ONLINE) ini adalah sebuah sistem pemesanan makanan online sederhana yang dibangun menggunakan bahasa pemrograman Java dan menerapkan konsep Pemrograman Berorientasi Objek (PBO). Program ini memiliki beberapa kelas utama seperti Restaurant, Login, Menu, dan Pesanan, serta menggunakan struktur data ArrayList untuk menyimpan data tempat makan.

Pada program ini, terdapat dua jenis pengguna, yaitu pelanggan dan administrator. Pelanggan memiliki kemampuan untuk melihat daftar tempat makan, memilih tempat makan, memesan makanan, dan melihat total harga pesanan mereka. Sementara itu, administrator memiliki wewenang untuk menambahkan dan menghapus tempat makan, serta melihat daftar tempat makan yang tersedia.

Untuk mengakses program, pengguna harus melakukan proses login. Proses ini menggunakan perulangan do-while untuk terus berjalan hingga pengguna memilih untuk keluar dari program. Setelah login berhasil, pengguna dapat memilih opsi menu yang tersedia.

Program ini menggunakan objek Scanner untuk membaca input dari pengguna, memfasilitasi interaksi antara pengguna dan program. Selain itu, program ini juga menggunakan konsep perulangan do-while untuk mengatur proses login dan memilih opsi menu, sehingga pengguna dapat terus berinteraksi dengan program sampai mereka memilih untuk keluar.

## UML

![UML](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/299afa8e-171e-4903-b2ea-273b49f00dd5)

untuk melihat UML lebih jelas ada pada dalam folder UML.

## Alur Program

### Login 

Program dimulai dengan permintaan login, di mana pengguna harus memilih status sebagai admin atau pelanggan, dan memasukkan username serta password yang telah ditentukan sebelumnya.

- Admin

```sh
username : admin
password : adminpw123
```

- Customer

```sh
username : customer
password : customerpw123
```

### Berikut ini tampilan awal dari menu login :

### Menu Login
Di bagian ini, dapat memilih login sebagai customer atau admin.

![LOGIN](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/78cdcf32-828f-4dcd-bfd3-93c253d9ce57)

#### Admin
- Login 
setelah menentukan pilihan admin akan muncul login admin dan masukkan username dan password untuk admin tersebut dan akan muncul menu admin

![LOGIN AS ADMIN](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/f6df21e9-a1e5-4cf8-b803-f76cf5c6cdc9)

Di menu pertama, pengguna diberi pilihan untuk menambahkan restaurant, menghapus restaurant yang sudah ada, atau melihat daftar tempat makan yang tersedia.

- Menambah Restaurant

![Tambah restoran dan menu di ADMIN](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/9f6c66a3-c3d0-4032-b833-9485b2592b79)

Gambar tersebut menunjukkan fungsi yang memungkinkan pengguna untuk menambahkan nama restaurant, daftar menu, beserta harganya.

- Menghapus Restaurant

![Hapus Restoran menu di ADMIN](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/cd497530-5d6d-4828-9e3a-33788d3358b5)

Dari restaurant yang sudah ditambahkan, dapat dihapus dengan memilih opsi 3 yaitu hapus restaurant dan diminta untuk menginput id restaurant yang ingin dihapus. Setelah diinput, restaurant akan terdelete.

- Melihat Restaurant

![Lihat Restoran menu di ADMIN](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/940d9f82-8ca4-45bd-9414-e3e9601d3183)

Untuk melihat daftar list restaurant, bisa pilih opsi pertama yaitu lihat restaurant dan akan muncul list tersebut yang telah kita tambahkan sebelumnya.

#### Customer

- Login Customer

![LOGIN AS CUSTOMER](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/8bc75181-a0da-43b2-a976-03947aea2d8d)


Untuk customer, mereka dapat memilih restaurant untuk dipesan dan kemudian memilih menu yang diinginkan. Setelah itu, sistem akan menampilkan total harga pesanan pada metoded lihat pesanan.

- Melihat Restaurant

![Lihat Restoran menu di ADMIN](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/940d9f82-8ca4-45bd-9414-e3e9601d3183)

Mirip dengan opsi 1 pada menu admin, customer dapat melihat daftar tempat makan yang telah ditambahkan oleh admin.

- Buat Pesanan dan memilih restaurant

![Buat Pesanan AS CUSTOMER](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/658ad428-1ace-4921-95c4-92bad23aceee)

Setelah memilih opsi 2, akan muncul lagi list restaurant dan customer akan memilih tempat makan yang diinginkan dan menu yang diingkaan. Akan diminta untuk menginput id restaurant, id menu, kuantitas, jarak, dan total harga sementara.

- Melihat pesanan

![Lihat Pesanan](https://github.com/Lucienthewizz/Tugas-PBO/assets/65484618/9322dd75-feca-409e-b042-05a518fb4568)

Setelah membuat pesanan, akan balik ke menu customer dan jika ingin melihat pesanan yang telah dibuat lanjut ke opsi 3 yaitu lihat pesanan. Setelah itu akan diberikan list yang telah diisi saat opsi 2 dan total dari pesanan yang telah dibuat akan ditotalkan.

Mohon maaf jika ada kesalahan dalam pengetikan, penjelasan, maupun gambar. Terimakasih sekian dari kami 🙏🏻
