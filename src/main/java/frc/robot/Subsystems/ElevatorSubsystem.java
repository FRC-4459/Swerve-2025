package frc.robot.Subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Commands.*;

public class ElevatorSubsystem extends SubsystemBase {
    public SparkMax elevatorMotor;
    public RelativeEncoder elevatorEncoder;
    public SparkMax elevatorMotor1;
    public RelativeEncoder elevatorEncoder1;
    public SparkMax elevatorMotor2;
    public RelativeEncoder elevatorEncoder2;
    public double distToBottom;
    public double distToTop;

    public ElevatorSubsystem() {
        elevatorMotor1 = new SparkMax(13, SparkMax.MotorType.kBrushless);
        elevatorEncoder1 = elevatorMotor1.getEncoder();
        elevatorMotor2 = new SparkMax(14, SparkMax.MotorType.kBrushless);
        elevatorEncoder2 = elevatorMotor2.getEncoder();
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
        elevatorEncoder1.setPosition(0);
        elevatorEncoder2.setPosition(0);
        // keep moving down until we stop rotating
        while ((elevatorEncoder1.getVelocity() > 0.1 || elevatorEncoder1.getVelocity() < -0.1) && 
        (elevatorEncoder2.getVelocity() > 0.1 || elevatorEncoder2.getVelocity() < -0.1)) {
            setElevatorSpeed(-0.5, false);
        }
        setElevatorSpeed(0, false);
        distToBottom = elevatorEncoder1.getPosition();
        while ((elevatorEncoder1.getVelocity() > 0.1 || elevatorEncoder1.getVelocity() < -0.1) &&
        (elevatorEncoder2.getVelocity() > 0.1 || elevatorEncoder2.getVelocity() < -0.1)) {
            setElevatorSpeed(0.5, false);
        }
        setElevatorSpeed(0, false);
        distToTop = elevatorEncoder1.getPosition();
    }

    public Command setElevatorPosition(double position) {
        return this.runOnce(() -> {
            ;
        });
    }

    public Command liftElevator() { 
        return new RunElevator(this, 0.8);
    }

    public Command dropElevator() {
        return new RunElevator(this, -0.8);
    }
}