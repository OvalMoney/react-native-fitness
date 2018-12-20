
# react-native-fitness
A React Native module to interact with Apple Healthkit and Google Fit.
Actually it is possible to fetch only steps and distance on a given period of time, in future other functionalities will be provided.

## Getting started

`$ npm install react-native-fitness --save`

### Mostly automatic installation

`$ react-native link react-native-fitness`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-fitness` and add `RNFitness.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNFitness.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.oval.fitness.RNFitnessPackage;` to the imports at the top of the file
  - Add `new RNFitnessPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-fitness'
  	project(':react-native-fitness').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-fitness/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-fitness')
  	```


## Usage

```javascript
import Fitness from 'react-native-fitness';

Fitness.isAuthorized()
        .then((authorized) => {
              //Do something
            })
            .catch((error) => {
              //Do something
            });
```
### Methods

- **isAuthorized**
Check if permissions are granted or not. It works on Android and iOS >= 12.0, while it will return with an error on other iOS.

- **requestPermissions**
Ask permission and return if user granted or not(Android), while, due to Apple's privacy model, always true is returned in iOS.

- **getSteps**
Fetch steps on a given period of time. It requires an Object with `startDate` and `endDate` attributes as string. If startDate is not provided an error will be thrown.

- **getDistance**
Fetch distance in meters on a given period of time. It requires an Object with `startDate` and `endDate` attributes as string. If startDate is not provided an error will be thrown.

### Attributes

- **Platform**
Return the used provider.


