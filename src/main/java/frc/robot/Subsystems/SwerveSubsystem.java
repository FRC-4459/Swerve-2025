package frc.robot.Subsystems;

import java.io.File;
import java.io.IOException;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DriverStation;
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
    // private AutoBuilder autoBuilder;
    private RobotConfig robotConfig;

    public SwerveSubsystem() {
        maximumSpeed = Units.feetToMeters(13.5);
        swerveJsonDirectory = new File(Filesystem.getDeployDirectory(), "swerve");

        try {
            swerveDrive = new SwerveParser(swerveJsonDirectory).createSwerveDrive(maximumSpeed);
            swerveDrive.setChassisDiscretization(true, 0.02);
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("that directory does not exist ;-;");
        }

        try {
          robotConfig = RobotConfig.fromGUISettings();
        } catch (Exception e) {
          // Handle exception as needed
          e.printStackTrace();
        }

        setupPathPlanner();

        SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
        swerveDrive.setHeadingCorrection(false);
        swerveDrive.setCosineCompensator(false);
        swerveDrive.setAngularVelocityCompensation(true, true, 0.1);
        
    }

    public void resetIMU() {
      swerveDrive.zeroGyro();
    }

    public void setupPathPlanner()
  {
      final boolean enableFeedforward = true;
      // Configure AutoBuilder last
      AutoBuilder.configure(
          this::getPose,
          this::resetPose,
          this::getRobotRelativeSpeeds,
          (speedsRobotRelative, moduleFeedForwards) -> {
            if (enableFeedforward)
            {
              swerveDrive.drive(
                  speedsRobotRelative,
                  swerveDrive.kinematics.toSwerveModuleStates(speedsRobotRelative),
                  moduleFeedForwards.linearForces()
                               );
            } else
            {
              swerveDrive.setChassisSpeeds(speedsRobotRelative);
            }
          },
          // Method that will drive the robot given ROBOT RELATIVE ChassisSpeeds. Also optionally outputs individual module feedforwards
          new PPHolonomicDriveController(
              // PPHolonomicController is the built in path following controller for holonomic drive trains
              new PIDConstants(.5, 0.0, 0.0),
              // Translation PID constants
              new PIDConstants(.8, 0.0, 0.0)
              // Rotation PID constants
          ),
          robotConfig,
          // The robot configuration
          () -> {
            // Boolean supplier that controls when the path will be mirrored for the red alliance
            // This will flip the path being followed to the red side of the field.
            // THE ORIGIN WILL REMAIN ON THE BLUE SIDE

            var alliance = DriverStation.getAlliance();
            if (alliance.isPresent())
            {
              return alliance.get() == DriverStation.Alliance.Red;
            }
            return false;
          },
          this
                           );
    // //Preload PathPlanner Path finding
    // // IF USING CUSTOM PATHFINDER ADD BEFORE THIS LINE
    // PathfindingCommand.warmupCommand().schedule();
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

    public Command driveCommand(DoubleSupplier translationX, DoubleSupplier translationY, DoubleSupplier angularRotationX)
      {
        return run(() -> {
          // Make the robot move
          swerveDrive.drive(SwerveMath.scaleTranslation(new Translation2d(
                                translationX.getAsDouble() * swerveDrive.getMaximumChassisVelocity(),
                                translationY.getAsDouble() * swerveDrive.getMaximumChassisVelocity()), 0.8),
                            Math.pow(angularRotationX.getAsDouble(), 3) * swerveDrive.getMaximumChassisAngularVelocity(),
                            true,
                            false);
        });
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

  public void driveFieldOriented(ChassisSpeeds velocity) {
    swerveDrive.driveFieldOriented(velocity);
  }

  public Command driveFieldOriented(Supplier<ChassisSpeeds> velocity) {
    return run(() -> {
      swerveDrive.driveFieldOriented(velocity.get());
    });
  }

  public Pose2d getPose() {
    return swerveDrive.getPose();
  }

  public void resetPose(Pose2d desired) {
    swerveDrive.resetOdometry(desired);
  }

  public ChassisSpeeds getRobotRelativeSpeeds() {
    return swerveDrive.getRobotVelocity();
  }

  public SwerveDrive getSwerveDrive() {
    return swerveDrive;
  }
}
