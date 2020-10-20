# Workout Companion App
        Created by Tamanji Che & Zakaria Ziouziou
## OverView
* **This app provides users with tools for aiding and encouraging their physical activities, staying fit and healthy.**
* ### [Demo Video](https://youtu.be/arGJ6GO-GS0)
## Features
[![home1.png](https://i.postimg.cc/HxWKfXzK/home1.png)](https://postimg.cc/VSTDtS3B) [![hrt.png](https://i.postimg.cc/nzksLbvr/hrt.png)](https://postimg.cc/ZBWYfQsz) [![diets.png](https://i.postimg.cc/7PjpsGvH/diets.png)](https://postimg.cc/LhBChs6w) [![diet2.png](https://i.postimg.cc/NFZ2386k/diet2.png)](https://postimg.cc/PvzqZwFC)

 [![chats.png](https://i.postimg.cc/jq6VLqwZ/chats.png)](https://postimg.cc/yWNpGHTZ) [![music.png](https://i.postimg.cc/506Rp1nQ/music.png)](https://postimg.cc/jwYcSVVR)

* Pedometer
* Heart Rate Monitor
* Diet Plans
* Music player
* Instant messageing with other users

## Usage
* ### SetUp Requirements
    * Android Studio: version >= 4.0
    * minSDKVersion 23
    * targetSDKVersion 30
    *           kotlinOptions {
                   jvmTarget = "1.8"
                }
* ### SetUp 
      git clone https://github.com/ambeche/WorkoutCompanion.git  // as https
      git clone git@github.com:ambeche/WorkoutCompanion.git // as ssh
* **Open project with Android Studio as an existing project, built and run the project with your android device**

## Technical Specifications
* ### Sensors
     * **Internal Sensors**
          * Step Counter
          * Accelerometer
     * **External Sensor**
          * external Heart rate sensor with Bluetooth LE communication

* ### Basic Components used
    * Activities
    * BroadcastReceiver
    * Service
    * Provider
* **Fragements implemented as well**
* ### Persistence
     * Room
     * SharedPreference
     * File
     * Network Storage - Firebase
* **Web Service**
     * web API For fetching diets plans
     * Firebase Authentication
* **Hardware - Camera**
* **Audio - Music Player**
* ### Architectural Designs
     * ViewModel & LiveData
* ### Libraries Used
     * MPAndroidCharts
     * CircularProgressBar
     * Lottie
     * Anko
