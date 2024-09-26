const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const fs = require('fs').promises; 
const path = require('path');

const app = express();
const port = 3000;

app.use(cors());
app.use(bodyParser.json());

const filePath = path.join(__dirname, 'assets', 'data.json');

app.put('/saveData', async (req, res) => {
    try {
        const newDataArray = req.body.data;

        // Read existing data from the file
        let existingData = [];
        try {
            const data = await fs.readFile(filePath, 'utf8');
            existingData = JSON.parse(data);
        } catch (parseError) {
            console.error(parseError);
            res.status(500).json({ error: 'Error parsing existing data' });
            return;
        }


        // Ensure existingData is an array
        if (!Array.isArray(existingData)) {
            existingData = [];
        }

        // Combine existing data with the new data
        const updatedData = existingData.concat(newDataArray);

        // Write the updated data back to the file
        await fs.writeFile(filePath, JSON.stringify(updatedData, null, 2), 'utf8');

        // Send a JSON response
        res.json({ message: 'Data saved successfully' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: 'Internal Server Error' });
    }
});

app.listen(port, () => {
    console.log(`Server is running at http://localhost:${port}`);
});

app.get('/getData', async (req, res) => {
    try {
      const data = await fs.readFile(filePath, 'utf8');
      const parsedData = JSON.parse(data);
      res.json({ data: parsedData });
    } catch (error) {
      console.error(error);
      res.status(500).json({ error: 'Error reading data' });
    }
  });