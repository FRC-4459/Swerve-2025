// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.File;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import swervelib.parser.SwerveParser;
import swervelib.SwerveDrive;

import frc.robot.Constants;
import frc.robot.Commands.*;
import frc.robot.Subsystems.*;

@SuppressWarnings("unused")
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private SwerveSubsystem swerveDrive;
  private final RobotContainer m_robotContainer;

  XboxController driverController;

  public Robot() {
    m_robotContainer = new RobotContainer();
    driverController = new XboxController(0);
    swerveDrive = new SwerveSubsystem();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
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

    Pose2d p = new Pose2d();
    swerveDrive.resetPose(new Pose2d(0, 0, new Rotation2d(0)));
    swerveDrive.resetIMU();
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

    Pose2d p = new Pose2d();
    swerveDrive.resetPose(new Pose2d(0, 0, new Rotation2d(0)));
    swerveDrive.resetIMU();

    swerveDrive.driveCommand(
      () -> MathUtil.applyDeadband(-driverController.getLeftY(), 0.1),
      () -> MathUtil.applyDeadband(-driverController.getLeftX(), 0.1),
      () -> MathUtil.applyDeadband(-driverController.getRightX(), 0.1),
      () -> MathUtil.applyDeadband(-driverController.getRightY(), 0.1)
    ).schedule();
  }

  @Override
  public void teleopPeriodic() {
    if (driverController.getAButton()) {
      swerveDrive.resetIMU();
    }
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
