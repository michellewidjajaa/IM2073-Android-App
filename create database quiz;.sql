create database quiz;
use quiz;

create table responses (
    questionNo int,
    choice varchar(1)
);

CREATE TABLE questions (
    questionNo INT PRIMARY KEY,
    questionText VARCHAR(2000),
    choiceA VARCHAR(2000),
    choiceB VARCHAR(2000),
    choiceC VARCHAR(2000),
    choiceD VARCHAR(2000),
    correctAnswer CHAR(1)
);

INSERT INTO questions VALUES 
(1, 'What is the capital of France?', 'Berlin', 'Madrid', 'Paris', 'Rome', 'C'),
(2, 'Which planet is known as the Red Planet?', 'Earth', 'Mars', 'Venus', 'Saturn', 'B'),
(3, 'How many continents are there on Earth?', '5', '6', '7', '8', 'C'),
(4, 'What is the largest mammal in the world?', 'Elephant', 'Blue Whale', 'Giraffe', 'Shark', 'B'),
(5, 'Who wrote "Hamlet"?', 'Charles Dickens', 'William Shakespeare', 'Mark Twain', 'Jane Austen', 'B'),
(6, 'What gas do plants absorb from the atmosphere?', 'Oxygen', 'Nitrogen', 'Carbon Dioxide', 'Hydrogen', 'C'),
(7, 'What is the boiling point of water?', '90°C', '80°C', '100°C', '120°C', 'C'),
(8, 'Which country hosted the 2020 Summer Olympics?', 'Japan', 'Brazil', 'China', 'Russia', 'A'),
(9, 'What is the chemical symbol for gold?', 'Ag', 'Au', 'Gd', 'Go', 'B'),
(10, 'Which language is the most widely spoken in the world?', 'English', 'Hindi', 'Spanish', 'Mandarin Chinese', 'D');

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
