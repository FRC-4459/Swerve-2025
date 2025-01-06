// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.RelativeEncoder;
import com.ctre.phoenix6.hardware.CANcoder;
import edu.wpi.first.wpilibj.ADIS16470_IMU;

import frc.robot.Constants;
import frc.robot.Commands.*;
import frc.robot.Subsystems.*;

@SuppressWarnings("unused")
public class RobotArchive extends TimedRobot {
  private Command m_autonomousCommand;

  private final RobotContainer m_robotContainer;

  SparkMax topRightDriver;
  SparkMax topRightSteering;
  SparkMax topLeftDriver;
  SparkMax topLeftSteering;
  SparkMax bottomRightDriver;
  SparkMax bottomRightSteering;
  SparkMax bottomLeftDriver;
  SparkMax bottomLeftSteering;

  RelativeEncoder bottomRightDriveEncoder;
  RelativeEncoder bottomRightSteeringEncoder;
  RelativeEncoder topRightDriveEncoder;
  RelativeEncoder topRightSteeringEncoder;
  RelativeEncoder bottomLeftDriveEncoder;
  RelativeEncoder bottomLeftSteeringEncoder;
  RelativeEncoder topLeftDriveEncoder;
  RelativeEncoder topLeftSteeringEncoder;

  CANcoder bottomLeftCANcoder;
  CANcoder bottomRightCANcoder;
  CANcoder topLeftCANcoder;
  CANcoder topRightCANcoder;

  ADIS16470_IMU imu;

  NetworkTable dashboardTable;
  // top right drive encoder entry, top right steering encoder entry, etc.
  //  i cant kjeep naming things well forever its so many characters
  NetworkTableEntry trdee;
  NetworkTableEntry trsee;
  NetworkTableEntry bldee;
  NetworkTableEntry blsee;
  NetworkTableEntry brdee;
  NetworkTableEntry brsee;
  NetworkTableEntry tldee;
  NetworkTableEntry tlsee;
  NetworkTableEntry gyro;

  ElasticDashboard dashboard;
  UpdateElastic updateDashboard;

  XboxController driverController;

  @SuppressWarnings("deprecation")
  public RobotArchive() {
    m_robotContainer = new RobotContainer();

    topRightDriver = new SparkMax(Constants.topRightDriveID, MotorType.kBrushless);
    topRightSteering = new SparkMax(Constants.topRightSteeringID, MotorType.kBrushless);
    topLeftDriver = new SparkMax(Constants.topLeftDriveID, MotorType.kBrushless);
    topLeftSteering = new SparkMax(Constants.topLeftSteeringID, MotorType.kBrushless);
    bottomRightDriver = new SparkMax(Constants.bottomRightDriveID, MotorType.kBrushless);
    bottomRightSteering = new SparkMax(Constants.bottomRightSteeringID, MotorType.kBrushless);
    bottomLeftDriver = new SparkMax(Constants.bottomLeftDriveID, MotorType.kBrushless);
    bottomLeftSteering = new SparkMax(Constants.bottomLeftSteeringID, MotorType.kBrushless);

    topRightDriver.setInverted(true);
    topRightSteering.setInverted(true);
    topLeftDriver.setInverted(true);
    topLeftSteering.setInverted(true);
    bottomRightDriver.setInverted(true);
    bottomRightSteering.setInverted(true);
    bottomLeftDriver.setInverted(true);
    bottomLeftSteering.setInverted(true);

    bottomRightDriveEncoder = bottomRightDriver.getEncoder();
    bottomRightSteeringEncoder = bottomRightSteering.getEncoder();
    topRightDriveEncoder = topRightDriver.getEncoder();
    topRightSteeringEncoder = topRightSteering.getEncoder();
    bottomLeftDriveEncoder = bottomLeftDriver.getEncoder();
    bottomLeftSteeringEncoder = bottomLeftSteering.getEncoder();
    topLeftDriveEncoder = topLeftDriver.getEncoder();
    topLeftSteeringEncoder = topLeftSteering.getEncoder();

    topRightCANcoder = new CANcoder(9);
    bottomRightCANcoder = new CANcoder(10);
    bottomLeftCANcoder = new CANcoder(11);
    topLeftCANcoder = new CANcoder(12);

    imu = new ADIS16470_IMU();

    dashboard = new ElasticDashboard();
    updateDashboard = new UpdateElastic(dashboard);


    // zero NEO Encoders
    bottomRightDriveEncoder.setPosition(0.0);
    bottomRightSteeringEncoder.setPosition(0.0);
    topRightDriveEncoder.setPosition(0.0);
    topRightSteeringEncoder.setPosition(0.0);
    bottomLeftDriveEncoder.setPosition(0.0);
    bottomLeftSteeringEncoder.setPosition(0.0);
    topLeftDriveEncoder.setPosition(0.0);
    topLeftSteeringEncoder.setPosition(0.0);

    // zero gyro
    imu.calibrate();

    dashboardTable = NetworkTableInstance.getDefault().getTable("ElasticDashboard");
    trdee = dashboardTable.getEntry("Top Right Drive Encoder");
    trsee = dashboardTable.getEntry("Top Right Steering Encoder");
    brdee = dashboardTable.getEntry("Bottom Right Drive Encoder");
    brsee = dashboardTable.getEntry("Bottom Right Steering Encoder");
    bldee = dashboardTable.getEntry("Bottom Left Drive Encoder");
    blsee = dashboardTable.getEntry("Bottom Left Steering Encoder");
    tldee = dashboardTable.getEntry("Top Left Drive Encoder");
    tlsee = dashboardTable.getEntry("Top Left Steering Encoder");
    gyro = dashboardTable.getEntry("Yaw");

    CommandScheduler.getInstance().schedule(updateDashboard);
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();

    double[] encoderPositions = {
      topRightCANcoder.getAbsolutePosition().getValueAsDouble(),
      bottomRightCANcoder.getAbsolutePosition().getValueAsDouble(),
      bottomLeftCANcoder.getAbsolutePosition().getValueAsDouble(),
      topLeftCANcoder.getAbsolutePosition().getValueAsDouble()
    };

    updateDashboard.setEncoderValues(encoderPositions);

    trdee.setDouble(topRightDriveEncoder.getPosition());
    trsee.setDouble(topRightSteeringEncoder.getPosition());
    brdee.setDouble(bottomRightDriveEncoder.getPosition());
    brsee.setDouble(bottomRightSteeringEncoder.getPosition());
    bldee.setDouble(bottomLeftDriveEncoder.getPosition());
    blsee.setDouble(bottomLeftSteeringEncoder.getPosition());
    tldee.setDouble(topLeftDriveEncoder.getPosition());
    tlsee.setDouble(topLeftSteeringEncoder.getPosition());
    System.out.println("Yaw: " + imu.getAngle());
    gyro.setDouble(imu.getAngle());
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void autonomousExit() {}

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {
    System.out.println(bottomRightDriveEncoder.getPosition());
  }

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
