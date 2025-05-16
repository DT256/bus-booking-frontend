# Bus Booking App

Ứng dụng đặt vé xe khách di động được phát triển bằng Android và Java.

## Design
https://www.figma.com/design/k3NG3wjGNHnD11APItjRQK/Bus-Booking?node-id=1-2&t=YkC6E3kRwAv1RsYY-0
## Cấu trúc thư mục

```
app/src/main/java/com/group8/busbookingapp/
├── activity/           # Chứa các Activity của ứng dụng
├── adapter/           # Chứa các Adapter cho RecyclerView và ViewPager
├── dto/               # Data Transfer Objects - các đối tượng chuyển đổi dữ liệu
├── fragment/          # Chứa các Fragment của ứng dụng
├── model/             # Chứa các Model/Entity của ứng dụng
├── network/           # Chứa các class liên quan đến network (API, Retrofit)
└── viewmodel/         # Chứa các ViewModel cho MVVM pattern
```

## Các thành phần chính

### Activity
- `MainActivity`: Activity chính của ứng dụng, quản lý bottom navigation và các fragment
- `LoginActivity`: Màn hình đăng nhập
- `SearchActivity`: Màn hình tìm kiếm chuyến xe (đã chuyển thành fragment)
- `TicketHistoryActivity`: Màn hình lịch sử vé (đã chuyển thành fragment)
- `ChatActivity`: Màn hình chat (đã chuyển thành fragment)

### Fragment
- `SearchFragment`: Fragment tìm kiếm chuyến xe
- `TicketHistoryFragment`: Fragment hiển thị lịch sử vé
- `ChatFragment`: Fragment chat với bot

### Adapter
- `BookingPagerAdapter`: Adapter cho ViewPager trong TicketHistoryFragment
- `ChatAdapter`: Adapter cho RecyclerView trong ChatFragment

### Model
- `Booking`: Model đại diện cho thông tin đặt vé
- `Trip`: Model đại diện cho thông tin chuyến xe
- `ChatMessage`: Model đại diện cho tin nhắn trong chat
- `ChatManager`: Quản lý lịch sử chat

### Network
- `ApiClient`: Cấu hình Retrofit client
- `ApiService`: Interface định nghĩa các API endpoint

### ViewModel
- `TicketHistoryViewModel`: ViewModel quản lý dữ liệu cho TicketHistoryFragment

## Tính năng chính

1. **Tìm kiếm chuyến xe**
   - Chọn điểm đi/đến
   - Chọn ngày đi
   - Hiển thị danh sách chuyến xe phù hợp

2. **Quản lý vé**
   - Xem lịch sử vé
   - Phân loại vé theo trạng thái (đang di chuyển/đã hoàn thành/đã hủy)
   - Đặt vé mới

3. **Chat hỗ trợ**
   - Chat với bot hỗ trợ
   - Các tính năng nhanh: tìm tuyến xe, xem vé, hủy vé

## Công nghệ sử dụng

- Android SDK
- Java 17
- MVVM Architecture
- ViewBinding
- Retrofit cho API calls
- ViewPager2
- Bottom Navigation
- Material Design Components

## Cài đặt

1. Clone repository
2. Mở project trong Android Studio
3. Sync Gradle
4. Build và chạy ứng dụng

## Yêu cầu hệ thống

- Android Studio Arctic Fox trở lên
- Android SDK 21 trở lên
- Gradle 7.0 trở lên
- JDK 8 trở lên

## Cấu trúc code

### Java Classes
- Sử dụng Java 8 features như Lambda expressions và Stream API
- Implement các interface như ViewModel, LiveData, Observer
- Sử dụng Retrofit Callback interface cho network calls
- Sử dụng ViewBinding cho view binding

### Layout Files
- XML layouts với Material Design components
- Data binding expressions trong layout files
- ConstraintLayout cho responsive UI

### Dependencies
```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.6.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'androidx.viewpager2:viewpager2:1.0.0'
}
``` 
