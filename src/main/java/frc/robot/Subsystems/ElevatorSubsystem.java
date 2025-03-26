package frc.robot.Subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.math.controller.PIDController;
import frc.robot.Commands.*;

public class ElevatorSubsystem extends SubsystemBase {
    private SparkMax elevatorMotor1;
    private RelativeEncoder elevatorEncoder1;
    private SparkMax elevatorMotor2;
    private RelativeEncoder elevatorEncoder2;
    // private double distToBottom;
    // private double distToTop;
    private double sprocketDiameterCM = 3.58; // arbitrary value for now

    public ElevatorSubsystem() {
        elevatorMotor1 = new SparkMax(13, SparkMax.MotorType.kBrushless);
        elevatorEncoder1 = elevatorMotor1.getEncoder();
        elevatorMotor2 = new SparkMax(14, SparkMax.MotorType.kBrushless);
        elevatorEncoder2 = elevatorMotor2.getEncoder();
    } 

    public RelativeEncoder getElevatorEncoder() {
        return elevatorEncoder1;
    }

    public double getCMRaised() {
        return (elevatorEncoder1.getPosition() + elevatorEncoder2.getPosition()) / 2 * sprocketDiameterCM;
    }

    public double getFeetRaised() {
        return getCMRaised() / 30.48;
    }

    public void zeroEncoders() {
        elevatorEncoder1.setPosition(0);
        elevatorEncoder2.setPosition(0);
    }

    public void setElevatorSpeed(double speed) {
        elevatorMotor1.set(speed);
        elevatorMotor2.set(-speed);
    }

    public void setElevatorSpeed(double speed, boolean flip) {
        elevatorMotor1.set(speed);

        if (flip) {
            elevatorMotor2.set(-speed);
        } else {
            elevatorMotor2.set(speed);
        }
    }

    public void calibrate() {
        this.zeroEncoders();

        // keep moving down until we stop rotating
        while ((elevatorEncoder1.getVelocity() > 0.1 || elevatorEncoder1.getVelocity() < -0.1) && 
        (elevatorEncoder2.getVelocity() > 0.1 || elevatorEncoder2.getVelocity() < -0.1)) {
            setElevatorSpeed(-0.5, false);
        }

        setElevatorSpeed(0, false);
        // distToBottom = elevatorEncoder1.getPosition();

        while ((elevatorEncoder1.getVelocity() > 0.1 || elevatorEncoder1.getVelocity() < -0.1) &&
        (elevatorEncoder2.getVelocity() > 0.1 || elevatorEncoder2.getVelocity() < -0.1)) {
            setElevatorSpeed(0.5, false);
        }

        setElevatorSpeed(0, false);
        // distToTop = elevatorEncoder1.getPosition();
    }

    public Command liftElevator() { 
        return new RunElevator(this, 0.8);
    }

    public Command dropElevator() {
        return new RunElevator(this, -0.8);
    }
}