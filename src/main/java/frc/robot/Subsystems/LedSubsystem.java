package frc.robot.Subsystems;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;

import java.util.ArrayList;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LedSubsystem extends SubsystemBase {

    private final Distance kLedSpacing = Meters.of(1 / 120.0);
    private final LEDPattern m_rainbow = LEDPattern.rainbow(255, 128);
    private final LEDPattern m_scrollingRainbow =
        m_rainbow.scrollAtAbsoluteSpeed(MetersPerSecond.of(1), kLedSpacing);

    private final AddressableLEDBuffer ledBuffer = new AddressableLEDBuffer(70);
    private final AddressableLED led = new AddressableLED(0);
    private long tick = 0;

    public ArrayList<Integer[]> flag = new ArrayList<Integer[]>();

    public int customAnimTick(long t, double speed) {
        // leds are 1/2 inch apart
        // scheduler called every 20 ms
        double spacing = 0.5;
        double timing = 0.02;
        int result = (int) ((t * timing) / spacing * speed);
        return result;
    }

    public int realMod(int num, int num2) {
        try {
            return ((num % num2) + num2) % num2;
        } catch (ArithmeticException e) {
            System.out.println(num);
            System.out.println(num2);
            return -1;
        }
    }

    public LedSubsystem() {
        led.setLength(ledBuffer.getLength());
        led.setData(ledBuffer);
        led.start();
        flag.clear();
        // default 
        flag.add(new Integer[]{0x20, 0x20, 0xff}); // light blue
        flag.add(new Integer[]{0xff, 0x00, 0xff}); // pink??
        flag.add(new Integer[]{0xff, 0x00, 0xff}); // pink??
        flag.add(new Integer[]{0xff, 0xff, 0xff}); // white
        flag.add(new Integer[]{0xff, 0xff, 0xff}); // white
        flag.add(new Integer[]{0xff, 0x00, 0xff}); // pink??
        flag.add(new Integer[]{0xff, 0x00, 0xff}); // pink??
        flag.add(new Integer[]{0x20, 0x20, 0xff});  // light blue
    }

    public Command startLed() {
        return this.run(() -> {
            tick += 1;
            int advance = customAnimTick(tick, 6);
            if (advance < 0) {
                advance = 0;
            }
            for(int i = 0; i < ledBuffer.getLength(); i++) {
                try {
                // ledBuffer.setRGB() is actually setGRB()
                ledBuffer.setRGB(
                    i,
                    this.flag.get(realMod(i - advance, flag.size()))[1],
                    this.flag.get(realMod(i - advance, flag.size()))[0],
                    this.flag.get(realMod(i - advance, flag.size()))[2]
                );
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(i);
                    System.out.println(advance);
                    throw new ArrayIndexOutOfBoundsException("Some parameters of i and advance are negative after realMod()");
                }
            }
            led.setData(ledBuffer); 
        });
    }
}
