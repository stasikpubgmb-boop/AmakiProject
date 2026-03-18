package com.amaki.module.setting;

/**
 * Slider настройка (double значение с min/max)
 */
public class DoubleSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double increment;

    public DoubleSetting(String name, double defaultValue, double min, double max, double increment) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.value = Math.max(min, Math.min(max, defaultValue));
    }

    @Override
    public void setValue(Double value) {
        this.value = Math.max(min, Math.min(max, value));
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getIncrement() {
        return increment;
    }

    /**
     * Возвращает значение в процентах от 0 до 1
     */
    public double getPercentage() {
        return (value - min) / (max - min);
    }

    /**
     * Устанавливает значение по проценту (0-1)
     */
    public void setPercentage(double percentage) {
        setValue(min + (max - min) * Math.max(0, Math.min(1, percentage)));
    }
}
