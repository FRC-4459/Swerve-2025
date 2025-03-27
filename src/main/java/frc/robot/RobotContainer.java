// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.DriverStation;
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
  private IntakeSubsystem intakeSubsystem = new IntakeSubsystem();
  private LedSubsystem ledSubsystem = new LedSubsystem();

  // the following code is shamelessly swiped from broncbotz 3481's YAGSL example code
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
    Command raiseElevatorL1 = new SetElevatorPosition(elevatorSubsystem, OperatorConstants.elevatorL1CM);
    Command raiseElevatorL2 = new SetElevatorPosition(elevatorSubsystem, OperatorConstants.elevatorL2CM);
    Command lowerElevator = new SetElevatorPosition(elevatorSubsystem, 0);
    Command output = new RunIntake(intakeSubsystem, 0.25, 0.4);
    Command intake = new RunIntake(intakeSubsystem, -0.25, 0.5);

    NamedCommands.registerCommand("RaiseElevatorL1", raiseElevatorL1);
    NamedCommands.registerCommand("Output", output);
    NamedCommands.registerCommand("LowerElevator", lowerElevator);
    NamedCommands.registerCommand("Intake", intake);

    DriverStation.silenceJoystickConnectionWarning(true);
    configureBindings();
    ledSubsystem.startLed().schedule();
  }

  private void configureBindings() {
    Command driveFieldOrientedDirectAngle = swerveDrive.driveFieldOriented(driveDirectAngle);
    Command driveFieldOrientedAnglularVelocity = swerveDrive.driveFieldOriented(driveAngularVelocity);
    Command driveRobotOrientedAngularVelocity = swerveDrive.driveFieldOriented(driveRobotOriented);
    Command driveFieldOrientedDirectAngleKeyboard = swerveDrive.driveFieldOriented(driveDirectAngleKeyboard);
    Command driveFieldOrientedAnglularVelocityKeyboard = swerveDrive.driveFieldOriented(driveAngularVelocityKeyboard);

    if (RobotBase.isSimulation()) {
      swerveDrive.setDefaultCommand(driveFieldOrientedDirectAngleKeyboard);
    } else {
      swerveDrive.setDefaultCommand(driveFieldOrientedAnglularVelocity);
    }

    driverController.rightBumper().whileTrue(elevatorSubsystem.dropElevator());
    driverController.leftBumper().whileTrue(elevatorSubsystem.liftElevator());

    driverController.rightTrigger().whileTrue(new RunIntake(intakeSubsystem, -0.25));
    driverController.leftTrigger().whileTrue(new RunIntake(intakeSubsystem, 0.25));

    driverController.x().onTrue(Commands.run(() -> elevatorSubsystem.zeroEncoders()));
    driverController.a().onTrue(Commands.run(() -> swerveDrive.resetIMU()));
  }

  public Command getAutonomousCommand() {
    return new PathPlannerAuto("barge1");
  }
}
