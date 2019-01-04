[strom]:https://www.github.com/w3-engineers/android-framework

# Telemesh

[![Build Status](https://travis-ci.com/w3-engineers/telemesh.svg?branch=master)](https://travis-ci.com/w3-engineers/telemesh)
[![Coverage Status](https://coveralls.io/repos/github/w3-engineers/telemesh/badge.svg?branch=master)](https://coveralls.io/github/w3-engineers/telemesh?branch=master)

A mesh network based off-grid messaging application supported by blockchain technology.

## Problem statement

Globally, 65 million people are forcibly displaced, and over 22 million are refugees. In Bangladesh, there are over 650,000 Rohingya refugees who have fled violence,
mass killings and sexual abuse from neighboring Myanmar.2 Of those, nearly 60% are children, many of whom are orphaned
Distributing information about humanitarian services to large numbers of refugees poses significant challenges for NGOs like UNICEF. While 40% of rural
refugee households have smartphones, many are unconnected due to a lack of or poor telecommunications infrastructure or unaffordable cellular
costs. The UNHCR believes that connecting refugees would ultimately transform humanitarian operations.

## Solution

We intend to make use of mesh network. It allows for multi-hop, peer-to-peer connectivity directly between smartphones, instead of
relying on internet and cell networks. Blockchain is used in the network to uniquely identify each node (smartphone) providing a trust layer to users without
centralized signup. It also provides the infrastructure for users to connect multiple separate meshes by sharing an internet connection in exchange for ERC20 tokens.
This offers an entirely new and unique method of information distribution not possible with existing technology.

For UNICEF, we plan to develop an open source messaging app to be tested in refugee camps, specifically, Cox’s Bazar, Bangladesh. A broadcast channel would allow
UNICEF to push vital information to smartphone users about services like vaccination clinics, maternity clinics and schools. The app would also allow refugees to
message one another even if they do not have a SIM card or cellular data.

### Prerequisites

Mesh networking technology.

## Feature list

* One to one messaging
* Message broadcast, multicast
* App sharing

### Description

* **[Strom](https://www.github.com/w3-engineers/android-framework)**: It is just a wrapper on native android to reduce some repeated works. This has been used as an dependency into this project.

* **Viper**: This is also a wrapper on Rightmesh and has been used as an dependency.

### Installing

We can use global **_gradle.properties_** file for **RM keys**. In general it could be found in the following location:
> **Linux:**  _home/\<usr>/.gradle_ 

> **Windows:** _C:/Users\<username>/.gradle/gradle.properties_.

> **Mac:**  _Users/\<usr>/.gradle_ 

In case if the file is not there then we can create one by the name: _gradle.properties_. The content of the file is username, password, and the App key:

> User name key is: _rightmesh_build_username_ 
> 
> User password is: _rightmesh_build_password_ 
> 
> App key is: _org.w3.telemesh_ 

## Deployment

TBD

## License

TBD

## Acknowledgments

TBD
