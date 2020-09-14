# collision-avoidance-app
A collision and avoidance detection system.

# Android Application
- ### Main Thread
    The main thread is responsible for synchronizing the android application. During the initialization the application asks for permissions. The android version used for the development was the Android 7 thus permissions must be asked runtime. Using the ```getMacAddress()``` method the topic at which the android client will be subscribed is initialized. At this point the main activity boots the mqtt client and defines a number of ```OnClickListeners``` to buttons. The application's interface also includes a ```settings``` button that opens a new layout. This layout contains three buttons to solely grant permissions from the user and an inteface for text input. Lastly there is an option to exit the application.
- ### Android Mqtt Client
    The main thread's context is passed as an argument to the ```AndroidMqttClient``` constructor. Even though the client is a non-activity class by using the above method it can produce messages in the application's inteface. 