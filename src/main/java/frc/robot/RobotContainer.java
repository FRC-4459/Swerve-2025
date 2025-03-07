// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import frc.robot.Commands.*;
import frc.robot.Subsystems.*;
import frc.robot.OperatorConstants;

import swervelib.SwerveInputStream;

@SuppressWarnings("unused")
public class RobotContainer {
  final CommandXboxController driverController = new CommandXboxController(0);
  private SwerveSubsystem swerveDrive = new SwerveSubsystem();
  private ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();

  // the following code is swiped from broncbotz 3481's YAGSL example code
  /**
   * Converts driver input into a field-relative ChassisSpeeds that is controlled by angular velocity.
   */
  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(swerveDrive.getSwerveDrive(),
                                                                () -> driverController.getLeftY() * -1,
                                                                () -> driverController.getLeftX() * -1)
                                                            .withControllerRotationAxis(driverController::getRightX)
                                                            .deadband(OperatorConstants.DEADBAND)
                                                            .scaleTranslation(0.8)
                                                            .allianceRelativeControl(true);

  /**
   * Clone's the angular velocity input stream and converts it to a fieldRelative input stream.
   */
  SwerveInputStream driveDirectAngle = driveAngularVelocity.copy().withControllerHeadingAxis(driverController::getRightX,
                                                                                             driverController::getRightY)
                                                           .headingWhile(true);

  /**
   * Clone's the angular velocity input stream and converts it to a robotRelative input stream.
   */
  SwerveInputStream driveRobotOriented = driveAngularVelocity.copy().robotRelative(true)
                                                             .allianceRelativeControl(false);

  SwerveInputStream driveAngularVelocityKeyboard = SwerveInputStream.of(swerveDrive.getSwerveDrive(),
                                                                        () -> -driverController.getLeftY(),
                                                                        () -> -driverController.getLeftX())
                                                                    .withControllerRotationAxis(() -> driverController.getRawAxis(
                                                                        2))
                                                                    .deadband(OperatorConstants.DEADBAND)
                                                                    .scaleTranslation(0.8)
                                                                    .allianceRelativeControl(true);
  // Derive the heading axis with math!
  // Collapse this if you like your eyes
  SwerveInputStream driveDirectAngleKeyboard = driveAngularVelocityKeyboard.copy().withControllerHeadingAxis(() ->
                                                                                                              Math.sin(
                                                                                                                  driverController.getRawAxis(
                                                                                                                      2) *
                                                                                                                  Math.PI) *
                                                                                                              (Math.PI *
                                                                                                               2),
                                                                                                          () ->
                                                                                                              Math.cos(
                                                                                                                  driverController.getRawAxis(
                                                                                                                      2) *
                                                                                                                  Math.PI) *
                                                                                                              (Math.PI *
                                                                                                               2))
                                                                               .headingWhile(true);

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    Command driveFieldOrientedDirectAngle = swerveDrive.driveFieldOriented(driveDirectAngle);
    Command driveFieldOrientedAnglularVelocity = swerveDrive.driveFieldOriented(driveAngularVelocity);
    Command driveRobotOrientedAngularVelocity = swerveDrive.driveFieldOriented(driveRobotOriented);
    Command driveFieldOrientedDirectAngleKeyboard = swerveDrive.driveFieldOriented(driveDirectAngleKeyboard);
    Command driveFieldOrientedAnglularVelocityKeyboard = swerveDrive.driveFieldOriented(driveAngularVelocityKeyboard);

    Command liftElevator = Commands.run(() -> elevatorSubsystem.setElevatorSpeed(0.8, false));
    Command dropElevator = Commands.run(() -> elevatorSubsystem.setElevatorSpeed(-0.8, false));

    if (RobotBase.isSimulation()) {
      swerveDrive.setDefaultCommand(driveFieldOrientedDirectAngleKeyboard);
    } else {
      swerveDrive.setDefaultCommand(driveFieldOrientedAnglularVelocity);
    }

    driverController.rightBumper().whileTrue(liftElevator);
    driverController.leftBumper().whileTrue(dropElevator);
  }

  public Command getAutonomousCommand() {
    return new PathPlannerAuto("erm");
  }
}
