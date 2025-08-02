// Test script for email generation logic
// This replicates the logic from Register.jsx

// Utility function to clean names for email generation
const cleanNameForEmail = (name) => {
  // Remove spaces, special characters, and normalize
  return name
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9]/g, '') // Remove all non-alphanumeric characters
    .substring(0, 20); // Limit length for practical email addresses
};

// Utility function to generate email safely
const generateEmail = (firstName, lastName) => {
  const cleanFirst = cleanNameForEmail(firstName);
  const cleanLast = cleanNameForEmail(lastName);
  
  // Check if we have valid characters left after cleaning
  if (!cleanFirst || !cleanLast) {
    return null; // Invalid name combination
  }
  
  const username = cleanFirst.substring(0, 1) + '.' + cleanLast;
  return username + '@ourcompany.com';
};

// Test cases
console.log('=== Email Generation Test ===\n');

// Test case 1: The specific names requested
const firstName = "BJ John";
const lastName = "De La Cruz";

console.log(`Test Case: "${firstName}" + "${lastName}"`);
console.log(`Cleaned first name: "${cleanNameForEmail(firstName)}"`);
console.log(`Cleaned last name: "${cleanNameForEmail(lastName)}"`);
console.log(`Generated email: "${generateEmail(firstName, lastName)}"`);
console.log('');

// Additional test cases to verify robustness
const testCases = [
  ["John", "Smith"],
  ["Mary", "O'Connor"],
  ["José", "García"],
  ["李", "王"],
  ["", "Smith"],
  ["John", ""],
  ["123", "456"],
  ["!@#", "$%^"],
  ["A B C", "D E F"],
  ["Jean-Pierre", "Van Der Berg"]
];

testCases.forEach(([first, last], index) => {
  console.log(`Test ${index + 2}: "${first}" + "${last}"`);
  const email = generateEmail(first, last);
  if (email) {
    console.log(`  ✅ Generated: ${email}`);
  } else {
    console.log(`  ❌ Failed: Unable to generate valid email`);
  }
});
