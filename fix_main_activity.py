import re

file_path = r'app/src/main/java/com/example/mathia/MainActivity.kt'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace correct with correctAnswer
content = content.replace('userAnswer.toIntOrNull() == correct', 'userAnswer.toIntOrNull() == correctAnswer')

# Replace n1/n2 assignment with generateNewQuestion()
pattern = r'n1\s*=\s*\(1\.\.15\)\.random\(\)\r?\n\s*n2\s*=\s*\(1\.\.n1\)\.random\(\)'
content = re.sub(pattern, 'generateNewQuestion()', content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("Replacement complete.")
