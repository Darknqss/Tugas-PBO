# TugasPBO2_API_Subscriptions

#### Nama Kelompok:
 Ida Bagus Agung Wiswa Pramana
- NIM: 2305551092

I Gusti Agung Ngurah Lucien Yudistira Purnawarman 
- NIM: 2305551152

#### Kelas: PBO kelas E

## Deskripsi Program

API (Application Programming Interface) adalah sekumpulan definisi dan protokol yang memungkinkan perangkat lunak atau aplikasi untuk saling berkomunikasi dan berbagi data satu sama lain. API berfungsi sebagai perantara yang memungkinkan aplikasi berbeda untuk berinteraksi dan menggunakan fungsi dari aplikasi lain tanpa harus memahami cara kerja internal aplikasi tersebut.

Secara sederhana, API memungkinkan pengembang untuk menggunakan kode atau layanan yang sudah ada untuk membangun aplikasi baru, sehingga mempercepat proses pengembangan dan meningkatkan efisiensi.

Program API dari java ini bertujuan untuk membangun API sederhana yang menangani operasi dasar (CRUD) untuk sistem manajemen Customers, item, dan langganan (subscriptions). API ini menggunakan database SQLite untuk menyimpan data dan memungkinkan pengguna untuk berinteraksi dengan sistem melalui permintaan HTTP. API dirancang untuk digunakan dalam skenario aplikasi yang memerlukan pengelolaan Customers dan langganan, seperti layanan berlangganan produk atau konten. 

Dengan method Request method pada API:
- GET: untuk mendapatkan list atau detail data dari entitas
- POST: untuk membuat data entitas baru
- PUT: untuk mengubah data dari entitas
- DELETE: untuk menghapus data dari entitas

Dan akan ditest pada aplikasi Postmen untuk menguji API tersebut


## Struktur Direktori


![Struktur Direktori](https://github.com/Lucienthewizz/Webdesign/assets/65484618/ed2522ec-0c1b-4a26-8ef1-8224afa8aceb)
## Alur Start

#### 1. Buka kelas main dan run, jika sudah berhasil maka akan muncul hasil seperti gambar dibawah
![image](https://github.com/Lucienthewizz/Webdesign/assets/65484618/8abd13e0-913a-4c9a-bcf4-d354ce430f56)

#### 2. Jika sudah terlihat, langkah selanjutnya tinggal copy 127.0.0.1:9152 atau http://localhost:9152. Lalu buka aplikasi postmen untuk menguji API

__NOTE: sesuai port yang diset, port yang kami set itu salah satu NIM dari kelompok kami yaitu NIM akhiran 152.__

![image](https://github.com/Lucienthewizz/Webdesign/assets/65484618/548e37c2-0d5a-42b8-8456-f3aeb8711ce3)

#### 3. Setelah membuka tinggal buat collection dan request. Setelah itu pilih metode yang di inginkan dan isi port yang tadi telah di copy. Contoh ada gambar dibawah.

![image](https://github.com/Lucienthewizz/Webdesign/assets/65484618/428a733a-4203-4cef-a258-53d1c43d8d4b)

#### 4. Selanjutnya Setelah mencopy, copy Autentikasi akses API menggunakan API key yg di hardcoded pada kelas Main dan click header, tambahkan key dan valueisikan Key yang telah diset yaitu "Authorization" dan valuenya "api_subscription".

- Key       : Authorization
- Value     : api_subscription

![image](https://github.com/Lucienthewizz/Webdesign/assets/65484618/d6af2c47-7302-4978-9fbf-b3f7f8060318)
![image](https://github.com/Lucienthewizz/Webdesign/assets/65484618/cf4310d9-4c43-46ec-9261-e65c51e8da28)

#### Setelah itu bisa langsung mengakses API api subscription!



## Spesifikasi API

### Customer
- GET Customers
    - GET /customers => daftar semua pelanggan ```(127.0.0.1:9152/customers)```

    ![GET Semua Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/22d441cd-d65a-4024-9d5f-ca2cd46c2b25)

    - GET /customers/{id} => informasi pelanggan dan alamatnya ```(127.0.0.1:9152/customers/1)```

    ![GET id=1 Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/48aaeae2-d135-426e-9e26-eb4b9762300f)

    - GET /customers/{id}/cards => daftar kartu kredit/debit milik pelanggan ```(127.0.0.1:9152/customers/1/cards)```

    ![GET cards Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/431540e6-d65a-4d58-b9b7-e266fba959a1)

    - GET /customers/{id}/subscriptions => daftar semua subscriptions milik pelanggan ```(127.0.0.1:9152/customers/1/subscriptions)```

    ![GET subcription semua Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/83fff7e4-5fa5-445e-91fb-ebedcb3bfc58)

    - GET /customers/{id}/subscriptions?subscriptions_status={active, cancelled,non-renewing} => daftar semua subscriptions milik pelanggan yg berstatus aktif / cancelled / non-renewing ```(127.0.0.1:9152/customers/1/subscriptions?subscriptions_status=active)```

    ![GET subcription status active Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/83fe6755-d556-471a-aa39-0ed150ff50a1)

- POST Customers
    - POST /customers => buat pelanggan baru  ```(127.0.0.1:9152/customers)```

    ![POST Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/7276e845-2198-40ac-b856-aaca8643e5d0)

- PUT Customers
    - PUT /customers/{id} ```(127.0.0.1:9152/customers/2)```

    ![PUT Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/38b80925-7b4c-44a2-9e68-105f841b64fb)

    - PUT /customers/{id}/shipping_addresses/{id} ```(127.0.0.1:9152/customers/3/shipping_addresses/3)```

    ![PUT Shipping Addresses Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/424eb71a-6097-4d34-8adb-f0877341f0ef)

- DELETE Customers 
    - DELETE /customers/{id}/cards/{id} => menghapus informasi kartu kredit pelanggan jika is_primary bernilai false ```(127.0.0.1:9152/customers/3/cards/3)```

    ![DELETE cards Costumer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/6098a631-ebb0-413c-b056-11ea22b743e3)

    - DELETE /customers/{id} ```(127.0.0.1:9152/customers/2)```

    ![DELETE Customer](https://github.com/Lucienthewizz/Webdesign/assets/65484618/066f0250-10b0-433a-ba0e-0611083ca77d)

### Items
- GET Items
    - GET /items => daftar semua produk ```(127.0.0.1:9152/items)```

    ![GET semua Item](https://github.com/Lucienthewizz/Webdesign/assets/65484618/6f303e55-cb58-43c8-a96b-7ae1c13a67a3)

    - GET /items?is_active=true => daftar semua produk yg memiliki status aktif ```(127.0.0.1:9152/items?is_active=true)```

    ![GET id=1 Item](https://github.com/Lucienthewizz/Webdesign/assets/65484618/7019ad10-c612-4bbe-95bf-70daa7ca9e1d)

    - GET /items/{id} => informasi produk ```(127.0.0.1:9152/items/1)```

    ![GET Status active true Item](https://github.com/Lucienthewizz/Webdesign/assets/65484618/3027a7dc-3a3a-43f7-8472-24acbe5a19be)

- POST Items
    - POST /items => buat item baru ```(127.0.0.1:9152/items/4)```

    ![POST Item set active to false](https://github.com/Lucienthewizz/Webdesign/assets/65484618/86271cdf-6773-459e-a697-b163d1531167)

- PUT Items
    - PUT /items/{id} ```(127.0.0.1:9152/customers/2)```

    ![PUT Item](https://github.com/Lucienthewizz/Webdesign/assets/65484618/3ca59b86-caef-4136-8304-5a04d9773094)

- DELETE Items
    - DELETE /items/{id} => mengubah status item is_active menjadi false ```(127.0.0.1:9152/items/4)```

    ![DELETE Item](https://github.com/Lucienthewizz/Webdesign/assets/65484618/beae256c-0207-43cd-b041-37295dcf912e)

### Subcriptions
- GET Subcriptions
    - GET /subscriptions => daftar semua subscriptions ```(127.0.0.1:9152/subscriptions)```

    ![GET  Subcriptions](https://github.com/Lucienthewizz/Webdesign/assets/65484618/a662897e-8a00-44ec-93bb-35c59aa7f2d5)

    - GET /subscriptions?sort_by=current_term_end&sort_type=desc => daftar semua subscriptions diurutkan berdasarkan current_term_end secara descending ```(127.0.0.1:9152/subscriptions?sort_by=current_term_end&sort_type=desc)```

    ![GET  Subcriptions DESC](https://github.com/Lucienthewizz/Webdesign/assets/65484618/644e48bc-4a55-4a94-bc7b-c32f7fe43521)

    - GET /subscriptions/{id} =>
        + informasi subscription,
        + customer: id, first_name, last_name,
        + subscription_items: quantity, amount,
        + item: id, name, price, type 
        ```(127.0.0.1:9152/subscriptions/7)```

    ![GET id =7  Subcriptions](https://github.com/Lucienthewizz/Webdesign/assets/65484618/a9750c9a-34e3-4f27-8e2c-9573fa5eca57)

- POST Subcriptions
    - POST /subscriptions => buat subscription baru beserta dengan id customer, shipping address, card, dan item yg dibeli ```(127.0.0.1:9152/subscriptions)``` 
    
    ![POST Subcriptions](https://github.com/Lucienthewizz/Webdesign/assets/65484618/6ad845d8-5616-4b7d-9da6-b809fef667a1)







#### Terimkasih dan maaf jika ada kesalahan dalam tugas 2 API Subscription 🙏

