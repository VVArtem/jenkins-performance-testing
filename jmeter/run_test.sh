#!/bin/bash
SCRIPT_PATH=$(dirname "$(readlink -f "$0")")
cd "$SCRIPT_PATH"

TIMESTAMP=$(date +%Y-%m-%d_%H-%m)
REPORT_DIR="reports/report_$TIMESTAMP"
LOG_FILE="results/test_result_$TIMESTAMP.jtl"

mkdir -p results reports

# 2. ОЧИЩЕННЯ (найважливіше): 
# Видаляємо папку, якщо вона раптом вже існує (хоча зазвичай timestamp унікальний)
rm -rf "$REPORT_DIR"
# Обов'язково видаляємо старий JTL файл, бо JMeter в нього дописує, а не перезаписує
rm -f "$LOG_FILE"

echo "Starting JMeter test at $TIMESTAMP..."

# Запуск (використовуємо автоматичний пошук jmeter через which)
/opt/apache-jmeter-5.6.3/bin/jmeter -n -t scripts/Essentials.jmx -l "$LOG_FILE" -e -o "$REPORT_DIR"

if [ $? -eq 0 ]; then
    echo "Success! Updating latest link..."
    # Оновлюємо посилання для Jenkins
    rm -rf reports/latest
    # Використовуємо копіювання, щоб 'latest' був повноцінною папкою
    cp -r "$REPORT_DIR" reports/latest
else
    echo "JMeter failed!"
    exit 1
fi