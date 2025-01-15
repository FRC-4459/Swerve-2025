package frc.robot.Subsystems;

import java.io.File;
import java.io.IOException;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.ADIS16470_IMU;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
import swervelib.parser.SwerveParser;
import swervelib.SwerveDrive;
import swervelib.math.SwerveMath;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;
import swervelib.SwerveModule;


public class SwerveSubsystem extends SubsystemBase {
    private double maximumSpeed;
    private File swerveJsonDirectory;
    private SwerveDrive swerveDrive;

    public SwerveSubsystem() {
        maximumSpeed = Units.feetToMeters(15);
        swerveJsonDirectory = new File(Filesystem.getDeployDirectory(), "swerve");

        try {
            swerveDrive = new SwerveParser(swerveJsonDirectory).createSwerveDrive(maximumSpeed);
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("that directory does not exist ;-;");
        }

        SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
        swerveDrive.headingCorrection = false;
    }

    public void resetIMU() {
      ADIS16470_IMU imu = (ADIS16470_IMU) swerveDrive.getGyro().getIMU();
      imu.reset();
    }

    public void driveDriveMotors(double setpoint) {
      SwerveModule modules[] = swerveDrive.getModules();
      modules[0].getDriveMotor().set(setpoint);
      modules[1].getDriveMotor().set(setpoint);
      modules[2].getDriveMotor().set(setpoint);
      modules[3].getDriveMotor().set(setpoint);
      modules[0].getAngleMotor().set(setpoint);
      modules[1].getAngleMotor().set(setpoint);
      modules[2].getAngleMotor().set(setpoint);
      modules[3].getAngleMotor().set(setpoint);
    }

  public Command driveCommand(DoubleSupplier translationX, DoubleSupplier translationY, DoubleSupplier headingX,
                              DoubleSupplier headingY)
  {
    return run(() -> {
      Translation2d scaledInputs = SwerveMath.scaleTranslation(new Translation2d(translationX.getAsDouble(),
                                                                                 translationY.getAsDouble()), 0.8);
      swerveDrive.drive(swerveDrive.swerveController.getTargetSpeeds(scaledInputs.getX(), scaledInputs.getY(),
                                                                      headingX.getAsDouble(),
                                                                      headingY.getAsDouble(),
                                                                      swerveDrive.getOdometryHeading().getRadians(),
                                                                      maximumSpeed));
    });
  }
}
