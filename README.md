[strom]:https://www.github.com/w3-engineers/android-framework
[travis]:https://travis-ci.com/w3-engineers/
[coverall]:https://coveralls.io/github/w3-engineers/telemesh

# Telemesh

[![Build Status](https://travis-ci.com/MimoSaha/telemesh.svg?branch=master)](https://travis-ci.com/MimoSaha/telemesh)
[![Coverage Status](https://coveralls.io/repos/github/MimoSaha/telemesh/badge.svg?branch=master)](https://coveralls.io/github/MimoSaha/telemesh?branch=master)

A mesh network based off-grid messaging application supported by blockchain technology.

## Project Overview
### Background
Globally, 65 million people are forcibly displaced, and over 22 million are refugees. In Bangladesh, there are over 650,000 Rohingya refugees who have fled violence, mass killings and sexual abuse from neighboring Myanmar.2 Of those, nearly 60% are children, many of whom are orphaned Distributing information about humanitarian services to large numbers of refugees poses significant challenges for NGOs like UNICEF. While 40% of rural refugee households have smartphones, many are unconnected due to a lack of or poor telecommunications infrastructure or unaffordable cellular costs. The UNHCR believes that connecting refugees would ultimately transform humanitarian operations.

### Project's Goal
We intend to make use of mesh network. It allows for multi-hop, peer-to-peer connectivity directly between smartphones, instead of relying on internet and cell networks. Blockchain is used in the network to uniquely identify each node (smartphone) providing a trust layer to users without centralized signup. It also provides the infrastructure for users to connect multiple separate meshes by sharing an internet connection in exchange for ERC20 tokens. This offers an entirely new and unique method of information distribution not possible with existing technology.

For UNICEF, we plan to develop an open source messaging app to be tested in refugee camps, specifically, Cox’s Bazar, Bangladesh. A broadcast channel would allow UNICEF to push vital information to smartphone users about services like vaccination clinics, maternity clinics and schools. The app would also allow refugees to message one another even if they do not have a SIM card or cellular data.

## Project's Structure

.

├── app 

│   └── src 

│       ├── main

│       │───└── com.w3engineers.unicef

│       │──────└── telemesh

│       │─────────└── data 				#*local database, file, shared preferences etc.*

│       │─────────└── ui            	#*ui components*

│       │──────└── util

│       │─────────└── helper        	#*Generic tasks like TimeUtil, NetworkUtil etc.*

│       │─────────└── lib           	#*third party library, component etc.*

│       │──────└── Application.java 	#*Android Application class*

├── viper                          		#*RightMesh wrapper module*

├── build.gradle

├── settings.gradle

├── gradle.properties





* **Alias**
N/A

* **Commands**
N/A

## Prerequisites
* Mesh networking technology. 
* Android device with Wifi, WifiDirect, Bluetooth or Bluetooth Low Energy (BLE) support.

## Project Dependencies
* **[Strom][strom]**: It is just a wrapper on native android to reduce some repeated works. This has been used as an dependency into this project.

* **Viper**: This is also a wrapper on Rightmesh and has been used as an dependency.

## Development environments
We are using the below environment for android mobile app development:
* Minimum API: 16 (Jelly Bean - 4.1.x)
* Java: 1.8.0_121
* Android Studio: 3.3 Stable
* Machine Used: Wnodws 10, macOS 10.14: Mojave (Liberty)

## How to get started
**Step 1: Clone repository:**
Navigate to directory where you want to keep source code. Open command prompt. Execute below command:
> git clone https://github.com/w3-engineers/telemesh.git

or if you want to download zip then [click here](https://github.com/w3-engineers/telemesh/archive/master.zip)

**Step 2: Prepare gradle.properties file:**
We can use global **_gradle.properties_** file for **RM keys**. In general it could be found in the following location:
> **Linux:**  _home/\<usr>/.gradle_ 

> **Windows:** _C:/Users\<usr>/.gradle/gradle.properties_.

> **MAC:** _~/Users/\<usr>/.gradle (By default this file is hidden in Mac. Press Command+Shift+. to see the hidden files)._

In case if the file is not there then we can create one by the name: _gradle.properties_. The content of the file is username, password, and the App key:

> User name key is: _rightmesh_build_username_ 
> 
> User password is: _rightmesh_build_password_ 
> 
> App key is: _org.w3.telemesh_ 

**Step 3: Sync and build:**
If everything is ok then sync and build should work as it should be. If not please recheck step 1 and 2. 

**Step 4: Test on device:**
[TBD]

## Test Coverage
* This repo is configured with [Travis][travis] and [coverall][coverall]. Every merge with *master* produced a test coverage report. Latest coverage report is available [here](https://coveralls.io/github/w3-engineers/telemesh?branch=master). [This](#Telemesh) badge here shows coverage status.

* To generate report locally you should go to project's root directory, then execute below command:
    > gradlew coveralls

**NOTE:** You must have a connected device or emulator as it runs instrumentation tests. You will find the coverage report at *telemesh/app/build/reports/coverage*

## Feature list
* One to one messaging
* Message broadcast, multicast
* App sharing
* More to come ...

## Description
TBD

## Deployment

TBD

## License

TBD

## Acknowledgments

TBD
