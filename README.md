# Implementation

you can clone this repository and import this project in Android Studio.

Using Gradle
In your ```build.gradle``` file of app module, add below dependency to import this library


```

dependencies {
	        implementation 'com.github.itsmemohsinali:UpiPayment:3.0.0'
}

```

If maven not installed, Then install maven in your project using below given code


```

allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}

```
