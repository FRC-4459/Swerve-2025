package frc.robot.Subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.math.controller.PIDController;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ElevatorSubsystem extends SubsystemBase {
    public SparkMax elevatorMotor;
    public RelativeEncoder elevatorEncoder;

    public ElevatorSubsystem() {
        elevatorMotor = new SparkMax(0, SparkMax.MotorType.kBrushless);
        elevatorEncoder = elevatorMotor.getEncoder();
    } 

}
