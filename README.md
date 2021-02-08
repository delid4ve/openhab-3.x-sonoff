# sonoff Binding

Allows Control/Updates of Ewelink based devices using the cloud and/or LAN.

## Supported Things

Currently known to support (non exhaustive):
UUID1: S20, S26, Basic, Mini, Mini PciE Card
UUID2: Unknown Models
UUID3: Unknown Models
UUID4: Unknown Models
UUID5: POW
UUID6: T11C, TX1C, G1
UUID7: T12C, TX2C
UUID8: T13C, TX3C
UUID9: Unknown Models
UUID15: TH10, TH16
UUID28: RFBRIDGE (Only sensors currently supported, awaiting remote logs)
UUID32: POWR2
UUID77: MICRO USB

Currently In Progress:
ZFBRIDGE, RFBRIDGE

## Discovery

Add your account thing and then run discovery.  All devices support automatic discovery.
For Sub devices, i.e sensors connected to an RF Bridge, add the main device and then run discovery again.

## Binding Configuration

Account Configuration:
username: Ewelink username
password: ewelink password
country code: country code of your phone number, i.i. +44,+00
Access Mode: local, mixed or cloud
IP Address: The IP Address of your openhab instance (used for local connection via multicast), in the format 192.168.0.2.  Please ensure multicast is enabled in your router and that you openhab istance is present on the same broadcast domain.
Polling Interval: interval in seconds to retreive non essential channels such as energy consumption and rssi. (minimum 30, -1 disables).

## Thing Configuration

Bridge sonoff:account:uniqueName "Sonoff Account" @ "myLocation" 
[ email="account@example.com", password="myPassword",countryCode="+00",accessmode="mixed",ipaddress="192.168.0.2",pollingInterval=60 ] {

32 		    GarageAirConditioning		    "Garage Air Conditioning" 	@ "thingLocation"	[ deviceId="1000bd9fe9" ]
32 		    SwimmingPoolHeatPump		    "Swimming Pool Heat Pump" 	@ "thingLocation"	[ deviceId="1000642d4d" ]
Bridge 		sonoff:28:uniqueName:RFBridge 	"RFBridge" 			        @ "thingLocation" 	[ deviceId="1000e72cb8" ] 
{
rfsensor	DoorContact		                "Door Contact"		        @ "contactLocation"	[ id="0" ]
rfsensor	WindowContact		            "Window Contact"	        @ "contactLocation"	[ id="1" ]
rfsensor	PIRSensor		                "PIR Sensor"		        @ "sensorLocation"	[ id="2" ]
}	
77		    USBSwitch			            "USB Switch"			    @ "thingLocation"	[ deviceId="1000dc155b" ]	
}

## Item Configuration

Group			Garage						"Garage"
Switch			GarageACSwitch				"Air Conditioning"				(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:switch"}
Number			GarageACCurrent				"Current"						(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:current"}
Number			GarageACVoltage				"Voltage"						(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:voltage"}
Number			GarageACPower				"Power"							(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:power"}
Number			GarageACToday				"Energy Usage Today"			(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:todayKwh"}
Number			GarageACYesterday			"Energy Usage Yesterday"		(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:yesterdayKwh"}
Number			GarageACSeven				"Energy Usage Last Week"		(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:sevenKwh"}
Number			GarageACThirty				"Energy Usage Last Month"		(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:thirtyKwh"}
Number			GarageACHundred				"Energy Usage Last Hundred"		(Garage) 			{channel="sonoff:32:uniqueName:GarageAirConditioning:hundredKwh"}
DateTime		GarageACLastOffline			"Last Offline"					(Garage)			{channel="sonoff:32:uniqueName:GarageAirConditioning:offlineTime"}
String			GarageACConnected			"Cloud Connected"				(Garage)			{channel="sonoff:32:uniqueName:GarageAirConditioning:online"}
Number			GarageACRssi				"Signal Stength"				(Garage)			{channel="sonoff:32:uniqueName:GarageAirConditioning:rssi"}

Group			Alarm						"Alarm"
DateTime		RFBridgeLastOffline			"Last Offline"					(Alram)				{channel="sonoff:28:uniqueName:RFBridge:offlineTime"}
String			RFBridgeConnected			"Cloud Connected"				(Alarm)				{channel="sonoff:28:uniqueName:RFBridge:online"}
Number			RFBridgeRssi				"Signal Stength"				(Alarm)				{channel="sonoff:28:uniqueName:RFBridge:rssi"}

Group			AlarmContacts				"Alarm Contacts"				(Alarm)
DateTime		DoorOpened					"Door Opened"					(AlarmContacts) 	{channel="sonoff:rfsensor:uniqueName:RFBridge:DoorContact:sensorTriggered"}
DateTime		WindowOpened				"Front Door Opened"				(AlarmContacts) 	{channel="sonoff:rfsensor:uniqueName:RFBridge:WindowContact:sensorTriggered"}
DateTime		MotionDetected				"Motion Detected"				(AlarmContacts) 	{channel="sonoff:rfsensor:uniqueName:RFBridge:PIRSensor:sensorTriggered"}

## Credits

Huge thanks to the following, this would not be possible without you:

https://github.com/skydiver

https://github.com/bwp91

https://github.com/AlexxIT

https://github.com/RealZimboGuy
