Written by Kingston Vastine. Started 12/20/24, a little while before 2025 season.
Put this file in a markdown viewer like Obsidian or Github Web to view the formatted text.

Firstly, the time that I'm beginning this devlog after finally getting the CAN loop working for all the components. At least I think I have.

This guide is meant to help through all of the electrical and programming work for setting up a [Swerve Drive Specialties mk4i drivetrain](https://www.swervedrivespecialties.com/products/mk4i-swerve-module), and a point of reference for if everything breaks down.

The mechanical part isn't included in here, because it's actually fairly easy - the [Instruction Manual](https://drive.google.com/file/d/1FBRBFIMH0lY_B9EJXfujerzmCWr5-Bwu/view) (it's at the bottom of the mk4i module's store page). Securing the modules together is a matter of cutting and drilling 4 identical 2x1 aluminum tubes and securing them to the module holes with bolts. [Here](https://cad.onshape.com/documents/29d54cac6bf4f98970e88e45/w/798c27bba961378971824544/e/67083672ee2e8a9a5ae9414c) is an onshape document that shows you how to do this, as well as the measurements for a belly pan.
## Prelude: Our Swerve Drive

To begin, there are a couple ways in which the swerve drive I'm currently working on differs from any other mk4i drivetrain.
- The side length (length of the aluminum tubes, as shown on the onshape document) are 18.25" instead of 21.5". We had to work with the little bit of aluminum we already had. This will affect the programming, but should work just fine.
- The bolts in the modules were not secured using loctite threadlocker, though the assembly guide recommends that you do so. It would have added too much time to the assembly process, and we barely got the chassis done under the wire as is. I hope to stress test the chassis as soon as possible to make sure that if this causes an issue we'll catch it before competition. I'll update this bullet if there are complications.
- We assembled modules fitted for Kracken x60 motors, and yet we only had Rev NEOs to go with them. This is because our anonymous sponsor ordered the wrong modules from the website. Seemingly, the NEOs work, though they are secured to the modules with 2 bolts instead of 3. If there is an issue with transmission of motion from the motors to the gears in the modules, this would be why. However, it hasn't caused any issues yet.
- The modules were secured to the tubing with 2 bolts on each corner, instead of 3. Once again, we were under tight time constrains (kind of our bad), and the drill press didn't cut quite straight enough for the bolts to thread through perfectly every time. This doesn't seem like it will cause any issues - the chassis seems quite sturdy as is - but it's definitely noteworthy. 

Those are the mechanical aspects of the build that may cause issues for us in the future. If you hear a weird noise, or something breaks, probably pay attention to these areas first.

Here are the specs of the build:
- mk4i modules with billet wheels and an L2 gear ratio, for kracken x60s (  :(  )
- 8 base rev NEOs (not neo vortexes)
- 8 rev Spark MAXes
- 4 CTRE CANcoders
- an NI Roborio 2.0 with an Analog Devices ADIS16470 IMU (we also have dedicated gyro, I'll have to see which is more usable later)
- A CTRE Power Distribution Panel

The aluminum tubes are the standard 2x1 .125" thickness tubes. They're 18.25" in length.


## Before 12/20/24 - Wiring!

On thursday, 12/19, me and Christina finished the robot chassis just before we had to leave. I took the chassis home, along with the near-complete electrical system built by Evan. The motor controllers were wired to the PDP but not the motors, and there were no CAN Connections.

##### Step 1: Motors to Motor Controllers
My first step when putting this thing together was to set out the electrical system and connect the motors to the motor controllers. There are a few methods of doing this: from best to worst, they are:

- Disconnects, end bits that you crimp (literally just smash/squeeze) onto the end of the wire. For some types, there's a male and female disconnect, and they come in pairs. For others both disconnects are the same. If anyone reading this wants to buy some in the future,  [these](https://www.andymark.com/products/powerpole-housings?quantity=100) powerpole connectors are highly rated by FRC teams. What I used for these connections were [these](https://www.acehardware.com/departments/lighting-and-electrical/boxes-fittings-and-conduit/lugs/33648?store=18516&gStoreCode=18516&gQT=1) disconnects. They're expensive but work fine.
- Heat shrink. If you have a heat gun, heat shrink generally works fine for making wire connections. Push both ends of the wire into the middle of the tube, and hit it with a heat gun to seal.
- Soldering. You can solder wire connections together with lead, even the bigger ones. Don't do this for super critical connections like the battery. Try to avoid it for everything. It's more complicated and time-consuming, as well as irreversible. If you want to know how to do it, get someone to teach you or find a tutorial. It's a lot to write.
- Electrical tape. If you're thinking of trying this, don't. It won't work, and if it does, it will stop working in the future.

I would have preferred to use disconnects for every wire, but I only had enough for about half - so I used the disconnects where I could, and used heat shrink for the rest. (I'm considering asking to order some power pole connectors and the crimp tool.) The connections are... sturdy enough. for now.

Wiring Spark MAXes to NEOs is fairly simple. Encoder cable (the black meshy one) goes to the encoder port, and the other wires are connected to their color. 

Wiring the Spark MAXes to the PDP requires some consideration. There are the big ports on the PDP - 40 amp max - and the smaller ones - 30 amp max. There are 8 of each type, and there are 8 motor controllers total. To place the motor controllers in ports, you have to consider how much each mechanism actually needs. The steering motor for each module doesn't need as much power as the drive motor, because it only handles the wheel rotation, which doesn't need to be lightning quick at all times. The drive motors, however, should get all the power we can provide them. Therefore, all of the steering motors should go on 30 amp ports (with 30 amp fuses) and the drive motors should go on 40 amp ports (with 40 amp fuses.) The fuse amperage needs to be correct, make sure you have the fuses you need.

##### Step 2: CANcoders

The CANcoders also require power, though they don't need much of it. There are two options for wiring the CANcoders: directly into the PDP, or into the VRM. Wiring a CANcoder to the PDP is simple: put it in any slot and give it a 20 amp breaker. Small slots are often preferred here because large slots are more valuable, but I don't think it matters. Wiring to the VRM is slightly trickier - the encoders only pull ~60-80 milliamps, so any slot in the VRM will provide enough amperage. They do, however, require 12 volts, so they need to be placed on the 12V side instead of the 5V side.  

I found that the CANcoder wires were quite short, meaning I had to wire two to the PDP and two to the VRM. The way I have it currently is definitely a temporary solution - the VRM is hanging in the air, suspended between the CANcoder wires. I need to extend the length of those. You can extend them with extra wire the same way you join any other two wires.

##### Step 3: CAN Bus

The wires that may give you the most headache are the blue-and-yellow CAN wires. These are data cables that you absolutely need to connect all of your components. The appeal of CAN is that you daisy-chain all of of your components together, making one neat circuit where every device is only wired once. Most CAN devices come with 2 wires, respectively having a male and female molex connector at the end.

Things you need to know about wiring the CAN circuit: 
- The CAN circuit must begin and end with the RoboRIO and PDP. Each of them have CAN slots, and you should begin by wiring the Rio to the first component. When you finish your circuit, the last component should be wired into the PDP slots. Be sure the PDP reset terminal (the little switch right above the CAN slots) is set to "on."
- If one node/device fails, the CAN network will cut off there. If your circuit looks like this:
	1 -------- 2 -------- 3 -------- 4 -------- 5
   and the device on 4 has issues, you will still see 1, 2, and 3 on the bus. 5, however, will not be on the bus, even if it works perfectly.

##### Yippee
By the end of 12/19, I had the drivetrain wired up fully. The wires weren't managed, and it was kind of a mess, but it was wired. All the lights flared up when I turned the thing on. Onto programming. Kinda.


## 12/20 - CAN Troubleshooting 0_0

Firstly, here's a writeup for how to troubleshoot the kinds of things that I went through today:

Once your CAN bus is wired, you'll want to look at it with your computer to see if all devices are visible. For all CTRE devices (CANcoders and the PDP), you can see all devices on the CAN network using Phoenix Tuner X from the Microsoft store.  Connect your computer to the RIO using a USB-B cable
![](https://upload.wikimedia.org/wikipedia/commons/thumb/6/62/USB_Type-B_plug_coloured.svg/800px-USB_Type-B_plug_coloured.svg.png)

and open up the tuner. Start your robot and wait a while for everything to boot. Refresh until you see "connected on 172.xxx.xx.xx". Run the diagnostic server program by clicking the green button. Once you do that, every CTRE device on the CAN bus should display, along with its CAN ID and name. Make sure everything has separate CAN IDs - I've done it myself, but if anything changes/resets, it may cause issues.

If you only see some of your components, that means a device along the way has failed, killing the CAN circuit there. Try to troubleshoot which device this is by removing all devices except those you know work, and then slowly re-adding until the circuit breaks again. Once it breaks, you know what you've just added doesn't work.

Looking at Spark MAXes is trickier. See, the program made to look at Spark MAXes and other rev products, [REV Hardware Client](https://docs.revrobotics.com/rev-hardware-client), is kind of bad. It looks nice but it's riddled with bugs. Personally, I've never been able to connect to the Spark MAXes on the CAN bus through the RIO connection.
What works for me is connecting to a Spark MAX with a USB-C cord directly. Plug the USB into both and refresh the hardware client. If it doesn't work, you should first change the laptop USB port that you've plugged the cord into. I don't know why it works, but it does sometimes. If that doesn't work, turn the robot off, unplug/replug the USB, and turn the robot back on. This works sometimes because the Spark MAXes have a CAN lockdown feature where they sometimes refuse to connect over USB with an active CAN connection. Finally, if that doesn't work, hold the button on the Spark MAX with a small alan wrench or paperclip, plug the USB in, and use the hardware client to update the firmware to the latest version.

Once you're connected to the Spark MAX, you should see its ID like in Phoenix Tuner. You should also see all of its configuration. **Make sure your smart current limits are set correctly.** Set your smart current limits on your motor controllers (so your fuses don't pop!), set the configuration in the dropdown to NEO brushless motors if they're not already, and burn the configuration using the button towards the bottom. Repeat with all the motor controllers. If your CAN bus is working, you should also see a "CAN Bus" device appear in the Rev Hardware Client. If all your motor controllers are working perfectly as well, you should see all of them listed under the CAN bus when you connect to one through USB.


Secondly, here was my experience:

I started up the robot, connected to the Rio, looked at Phoenix Tuner, and saw only one CANcoder, the one directly connected to the Rio. We had similar problems with CAN last year, so I sighed a bit to myself. The first thing I did was disconnect the Spark MAXes and connect all of the CANcoders directly to each other, making a complete circuit that looked like Rio - encoder - encoder - encoder - encoder - pdp. When I power cycled the robot and refreshed tuner, I saw all four CANcoders appear, as well as the PDP. I gave them all names and separate CAN IDs and began to reconnect Spark MAXes one by one.

I found that the top-right steering motor controller was giving me CAN issues, because when that one was excluded from the circuit, everything else worked. I re-flashed it with the latest firmware, but it didn't help. I read around on Chief Delphi and found that some old and even some new Spark MAX CAN wires have shorts in them, frying the connections. I cannibalized a CAN wire from an old Spark MAX and replaced the one on the faulty controller. Finally, the first part of the circuit (top left and bottom left CANcoder, 4 left motor controllers) seemed to work. However, the circuit cut off at the bottom right CANcoder, leaving the last CANcoder and the PDP out.

I repeated this process for the right side, finding the faulty Spark MAX replacing its CAN connector. After this, the complete CAN circuit seemed to work - I actually saw all the motor controllers on the hardware client !!!!!!!!!!! Finally, I could begin programming.

## 12/21 - Programming Begins.

Now's the hardest part. I'm following the guides laid out at Yet Another Generic Swerve Library's documentation. Today's entry is going to be less helpful for troubleshooting and more like a regular log.

But before I begin to follow their guide, I had to ensure everything actually worked. And, of course, it didn't. The first issue I ran into happened when I started looking at the documentation for REVLib, the third party library with the code to use Spark MAXes. There was a mismatch between the import path that I had in my own WPILib installation - `com.revlib.CANSparkMax` - and the path in the documentation - `com.revlib.spark.SparkMax`. I got a bit frustrated (RLCraft really takes it out of you) and resorted to asking a question on the FRC discord pretty early (they're a good resource if you're stumped.) Turns out that after the 2024 season, Rev Robotics replaced all their existing documentation with preview documentation for their 2025 REVLib. 

It turned out that in order to install the beta REVLib, I had to install the beta WPILib, and the beta NI Game Tools... etc. If you're working on the robot during off season, keep in mind that you may need to run the beta builds for your tools. After fully updating my software environment, as well as flashing all of the Spark MAXes and CANcoders with 2025 beta firmware, the import paths matched and I could actually code.

The first thing I did was test that all the motors actually worked. I ran all the steering motors at once, and they moved together. Then I ran all the drive motors, and one was missing. The wire connections were definitely not secure enough with the heat shrink. I'd have to go to a hardware store and pick some disconnects up tomorrow. But the motor isn't super necessary for the work I'm going to do today, so I pressed on.

I knew that some sort of nice data visualization was going to be required for this season, with all the complexities of swerve and how difficult it is to debug. So I browsed all the FRC information board programs packaged with NI game tools and decided to use Elastic. It was new and had a nice, modern design. I rooted through the WPILib documentation to read about the network tables API that all the info board programs used to gather info.

I threw together a subsystem to bind swerve encoder positions to a network table, and a command to actually manage and update that data. I implemented these two on the assumption that the positions would be floats representing the -50 to 50 values of the encoders as displayed in Phoenix Tuner. After I finished those, I went to Robot.java and was surprised to learn that CANcoder.getAbsolutePosition() gives you a wpi.Units.Angle object, which I had no idea how to work with. The documentation was honestly quite confusing to me. So I extended this with getAboslutePosition.getValueAsDouble() to get the value that I actually expected.

I put together a function that prints these values so I could physically confirm they work. This is where the work for today - about 4 hours of it ;-; - ended. Tomorrow, I'll have to reconnect those motors (hopefully my wallet doesn't explode having to buy wire disconnects) and convert that command and that subsystem to use doubles. I also read about an integration with YAGSL that gives you an easy drag-and-drop "swerve" elastic module that displays your encoder positions. Whether I do my implementation first or forgo it to begin working with YAGSL is to be determined. That's all!

## 12/26 - Time for YAGSL

I finally tidied up all the electrical and mechanical work today to have a 100% working / reliable system. yippee! This means I can dive headfirst into the programming. Like I said, I'm following YAGSL's guide to setting up your swerve - and the first entryworthy page in the guide is the "[Getting to know your robot](https://docs.yagsl.com/configuring-yagsl/getting-to-know-your-robot)" page in the "configuring" section. Below are tables of all the information that it asks for.

#### Main table

| Item                                                                         | Value                                                   |
| ---------------------------------------------------------------------------- | ------------------------------------------------------- |
| Drive Gear Ratio                                                             | mk4i's L2 - which is 6.75:1, as noted on their website. |
| Steering Gear Ratio                                                          | 150/7:1 or 21.42:1                                      |
| Absolute Encoder Ticks Per Revolution                                        | 1                                                       |
| CAN Bus Name                                                                 | `rio`                                                   |
| CAN IDs of motor controllers and encoders                                    | See Below                                               |
| Inversion states of the motors                                               | See Below                                               |
| Inversion state of the gyroscope                                             | See Below                                               |
| Absolute Encoder Offsets                                                     | See Below                                               |
| Motor Controller PID Values                                                  | TBD                                                     |
| Distance in inches from the center of your robot to the center of each wheel | ~14.5 inches                                            |
|                                                                              |                                                         |
#### Encoder Inversion Table
According to [this guide](https://docs.yagsl.com/bringing-up-swerve/creating-your-first-configuration)

| Module       | Drive    | Azimuth / Steering (built-in) | Absolute Encoder |
| ------------ | -------- | ----------------------------- | ---------------- |
| Top Right    | inverted | inverted                      | normal           |
| Bottom Right | inverted | inverted                      | normal           |
| Bottom Left  | inverted | inverted                      | normal           |
| Top Left     | inverted | inverted                      | normal           |
| Gyro         | ~        | normal                        | ~                |
#### CAN ID Table

| Device                                                                                  | CAN ID |
| --------------------------------------------------------------------------------------- | ------ |
| Top Left Steering Controller                                                            | 1      |
| Bottom Left Drive Controller                                                            | 2      |
| Top Left Drive Controller                                                               | 3      |
| Bottom Left Steering Controller                                                         | 4      |
| Top Right Steering Controller                                                           | 5      |
| Bottom Right Steering Controller                                                        | 6      |
| Top Right Drive Controller                                                              | 7      |
| Bottom Right Drive Controller                                                           | 8      |
| Top Left CANcoder                                                                       | 9      |
| Top Right CANcoder                                                                      | 10     |
| Bottom Left CANcoder                                                                    | 11     |
| Bottom Right CANcoder                                                                   | 12     |
| the orientations of the modules are relative to the front of the RIO (where the IMU is) | ~      |
#### CANcoder offset table

| Module       | CANcoder Offset |
| ------------ | --------------- |
| Top Right    | -0.26           |
| Bottom Right | 0.2             |
| Bottom Left  | 0.4             |
| Top Left     | 0.329           |

I spent most of my time today gathering values for these tables, using Elastic and the NetworkTables API to view the values themselves. I also had to invert the motors as defined in the inversion tables above - specifically, I had to call .setInverted(true) on all the Spark MAXes. Though, it is giving me a deprecated error. I'll fix it when I properly begin the swerve code - I'm just content that all the encoders work properly for now.

The code that I end with tonight will be up on the team [Github](https://github.com/FRC-4459/) as the first commit on the "Swerve 2025" repo. If you want a point of reference for using the basic hardware APIs or the NetworkTables API that first commit is where you can look.

The work today was relatively straightfoward - I followed the guides as written on the first few pages of the YAGSL wiki. I stopped just before the creation of the configuration JSON files, after importing all of the various libraries.

As I was writing this, I decided to build the project to make sure nothing broke and all of the dependencies have invalid years. Uh oh.

----------------------

Okay, I fixed the dependencies. Here's what I found out.

YAGSL requires basically every third party FRC library, because it's compatible with everything. You'll have to pick up everything on the [dependency page](https://docs.yagsl.com/configuring-yagsl/dependency-installation) plus "Studica", "ThriftyLib", and "MapleSim."
Studica and MapleSim all had tabs in the Vendor Dependencies tab in WPILIB VSCode. I didn't know that existed until a few minutes ago, but it's there. It should be towards the bottom of the VSCode sidebar once you have a project open. I updated everything to the latest once I got it from there, but that broke something with ReduxLib, which I then had to downgrade. If you're getting dependency errors, try to downgrade the dependency at the top of your error log.
It took me a moment to find the ThriftyLib page, but it's [here.](https://docs.home.thethriftybot.com/)
Finally, if you're writing code between seasons, you may have to go into the vendordeps folder and manually change the "frc year" number in some of the .json files so building doesn't throw an error. This may cause you problems. I had to do it for one library, and I'm not sure if it will break anything yet (but it built fine!)

With all the dependencies fixed, it's late and I'm going to bed. Tomorrow the programming actually starts but for real this time. :100:


## 12/27 - Getting Things Moving

Writing this after the fact. All things considered, getting the thing moving was very easy. YAGSL blackboxes most of the real hard stuff when it comes to swerve. I entered all the data I'd collected into the configuration generator, then set the swerveDrive.driveCommand() with the stick axes and deadbands. It got the thing moving with some nice data in Elastic. I did PID tuning to get the thing moving *correctly* - it took a while to go over the examples on WPILIB and get everything right. I think the drive PIDF is just fine, but the steering is a bit subpar. This is where I'm going to leave it out until we get back to the lab. Yippee
