const fs = require('fs');
const { parse } = require('csv-parse/sync');

class CsvFeeder {
    static loadUsers(filePath) {
        const fileContent = fs.readFileSync(filePath, 'utf8');

        const records = parse(fileContent, {
            columns: true, 
            skip_empty_lines: true,
            trim: true
        });

        return records;
    }
}

module.exports = CsvFeeder;