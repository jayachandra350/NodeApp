const express = require("express");
const path = require("path");
const { MongoClient } = require("mongodb");
require("dotenv").config(); // Load environment variables from .env file

const app = express();

// MongoDB Configuration
const mongoUrl = `mongodb://${process.env.DB_HOST}:${process.env.DB_PORT}`;
const dbName = process.env.DB_NAME;
let db;

// Connect to MongoDB
MongoClient.connect(mongoUrl, { useNewUrlParser: true, useUnifiedTopology: true })
  .then((client) => {
    console.log("Connected to MongoDB");
    db = client.db(dbName);
  })
  .catch((err) => {
    console.error("MongoDB connection failed:", err);
    process.exit(1);
  });

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Routes
app.get("/", (req, res) => {
  res.send("Hello World! Node.js App with MongoDB");
});
app.use(express.static(path.join(__dirname, "public")));
app.get("/users-data", async (req, res) => {
    try {
        const users = await db.collection("users").find().toArray();
        res.json(users); // Return users as JSON
    } catch (err) {
        console.error("Error fetching data from MongoDB:", err);
        res.status(500).send("Failed to fetch users.");
    }
});

// Test Route to Add a User to MongoDB
app.post("/add-user", async (req, res) => {
  const { name, email } = req.body;

  if (!name || !email) {
    return res.status(400).send("Name and Email are required.");
  }

  try {
    const result = await db.collection("users").insertOne({ name, email });
    res.send(`User added with ID: ${result.insertedId}`);
  } catch (err) {
    console.error("Error inserting data into MongoDB:", err);
    res.status(500).send("Failed to add user.");
  }
});

// Test Route to Fetch Users
app.get("/users", async (req, res) => {
  try {
    const users = await db.collection("users").find().toArray();
    res.json(users);
  } catch (err) {
    console.error("Error fetching data from MongoDB:", err);
    res.status(500).send("Failed to fetch users.");
  }
});

// Start Server
const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
app.get("/form", (req, res) => {
  res.sendFile(path.join(__dirname, "form.html"));
});
app.post("/add-user", async (req, res) => {
  const { name, email } = req.body;

  if (!name || !email) {
    return res.status(400).send("Name and Email are required.");
  }

  try {
    const result = await db.collection("users").insertOne({ name, email });
    res.send(`User added with ID: ${result.insertedId}`);
  } catch (err) {
    console.error("Error inserting data into MongoDB:", err);
    res.status(500).send("Failed to add user.");
  }
});

