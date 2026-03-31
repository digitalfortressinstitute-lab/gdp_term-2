# React Native & NativeWind — Class Exercises Workbook
**Beginner Level | 16 Sections | Hands-On Practice**


---

## Section 1 — What Is React Native?

### Exercise 1.1 — True or False
Write **TRUE** or **FALSE** next to each statement.

| # | Statement | Your Answer |
|---|-----------|-------------|
| 1 | React Native lets you build mobile apps using JavaScript. | |
| 2 | You need separate codebases for Android and iOS in React Native. | |
| 3 | React (without "Native") is used for building things in a web browser. | |
| 4 | React Native apps run inside a web browser like Chrome. | |
| 5 | Writing code once in React Native can produce both an Android and iPhone app. | |

---

### Exercise 1.2 — Fill in the Blank
Complete each sentence using the words in the box.

> **Word Bank:** `JavaScript` &nbsp;|&nbsp; `browser` &nbsp;|&nbsp; `mobile` &nbsp;|&nbsp; `Facebook/Meta` &nbsp;|&nbsp; `once`

1. React Native is a tool that lets you build ______________ apps for your phone.
2. React was originally created by ______________ for building web apps.
3. Regular React runs in a ______________, while React Native runs on a phone.
4. With React Native, you write your code ______________ and it works on both platforms.
5. Both React and React Native use ______________ as their programming language.

---

### Exercise 1.3 — Short Answer
Answer in 1–2 sentences in your own words.

**Q1. What is the biggest advantage of using React Native instead of building two separate apps?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

**Q2. What kind of apps does regular React build, and what does React Native build?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

---

## Section 2 — What Is Regular React?

### Exercise 2.1 — Complete the Comparison
Fill in the missing column based on what you know.

| | Regular React | React Native |
|---|---|---|
| **Runs on** | Web browser | |
| **Created by** | | Facebook/Meta |
| **Primary language** | | |
| **Used for** | | Mobile apps |

---

### Exercise 2.2 — Short Answer

**Q1. What does "library" mean in "React is a JavaScript library"?**

_Answer:_ ___________________________________________________________________________

**Q2. Can you use React Native to build a website? Why or why not?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

---

### Exercise 2.3 — Discuss
React and React Native look very similar in code but have important differences.

List **two things** that are the same between them, and **two things** that are different.

| Same | Different |
|------|-----------|
| 1. | 1. |
| 2. | 2. |

---

## Section 3 — Key Terms & Glossary

### Exercise 3.1 — Match the Term to Its Definition
Write the correct letter next to each term.

| Term | Letter |
|------|--------|
| Component | |
| JSX | |
| Props | |
| State | |
| Styling | |
| View | |
| Text | |
| StyleSheet | |
| NativeWind | |
| className | |

**Definitions:**

- **A** — React Native's built-in way to write styles in JavaScript objects.
- **B** — Settings passed into a component to customize it (like a color or a label).
- **C** — A library that brings Tailwind CSS class names into React Native.
- **D** — A piece of your app's screen, like a LEGO brick.
- **E** — Information the app remembers that can change over time; causes the screen to update.
- **F** — A special syntax that looks like HTML but is written inside JavaScript.
- **G** — How you control colors, sizes, spacing, and fonts.
- **H** — In React Native, the equivalent of a `<div>` in HTML — a container box.
- **I** — The attribute used in regular React (web) to apply CSS class names.
- **J** — In React Native, all words on screen must be inside this component.

---

### Exercise 3.2 — Explain in Your Own Words
Describe each term as if you were explaining it to a classmate who has never coded before.

**Component:**

___________________________________________________________________________________

**State:**

___________________________________________________________________________________

**Props:**

___________________________________________________________________________________

**JSX:**

___________________________________________________________________________________

---

### Exercise 3.3 — Use the Terms
Write a sentence using each of the following terms correctly in context.

1. *Component:* _____________________________________________________________________
2. *Props:* __________________________________________________________________________
3. *State:* __________________________________________________________________________

---

## Section 4 — Part 1: Regular React (For Websites)

### Exercise 4.1 — Reading Code
Study the code below and answer the questions underneath it.

```jsx
function UserCard() {
  return (
    <div style={{ padding: 20, backgroundColor: '#f0f0f0', borderRadius: 10 }}>
      <h1 style={{ fontSize: 24, color: '#333' }}>John Doe</h1>
      <p style={{ fontSize: 16, color: '#666' }}>Software Developer</p>
      <button style={{ backgroundColor: 'blue', color: 'white', padding: '10px 20px' }}>
        Follow
      </button>
    </div>
  );
}
```

1. What HTML element acts as the outer container? ______________________
2. What does `backgroundColor: '#f0f0f0'` do? ______________________
3. How are styles applied in this code? ______________________
4. What would you change to make the heading text red? ______________________
5. What does `borderRadius: 10` do to the container? ______________________

---

### Exercise 4.2 — Spot the Mistake
Each snippet has one error. Identify the mistake and write the correct version.

**Snippet A:**
```jsx
function Greeting() {
  return (
    <div>
      Hello World
    </div>
  );
}
```
_This is actually valid in regular React (web). Why would this same code fail in React Native?_

_Answer:_ ___________________________________________________________________________

**Snippet B:**
```jsx
function Card() {
  return (
    <div style='padding: 20px; color: red;'>
      <h1>Title</h1>
    </div>
  );
}
```
_Problem:_ __________________________________________________________________________

_Fixed:_ ____________________________________________________________________________

---

### Exercise 4.3 — Write It
Write a simple React (web) component called `AnnouncementCard`. It should display:
- A heading: "School Trip Friday!"
- A paragraph: "Remember to bring your permission slip."
- A button: "Got it"

Use inline styles. No Tailwind yet.

```jsx
function AnnouncementCard() {
  return (
    // Write your JSX here



  );
}
```

---

## Section 5 — Part 2: React Native (For Mobile Phones)

### Exercise 5.1 — Web vs Mobile Tag Translation
For each regular React (web) element, write the correct React Native equivalent.

| Regular React (Web) | React Native Equivalent |
|---------------------|------------------------|
| `<div>` | |
| `<p>` | |
| `<h1>` | |
| `<button>` | |
| `<input type="text" />` | |
| `<img src="..." />` | |
| `className="..."` | |
| `onClick={...}` | |

---

### Exercise 5.2 — Rewrite in React Native
Convert this web React component to React Native. Use `StyleSheet.create()` for styles and replace all HTML tags with the correct React Native components.

**Original (Web React):**
```jsx
function ProfileCard() {
  return (
    <div style={{ padding: 16, backgroundColor: '#fff', borderRadius: 8 }}>
      <h1 style={{ fontSize: 20, color: '#111' }}>Jane Smith</h1>
      <p style={{ fontSize: 14, color: '#666' }}>UI Designer</p>
      <button style={{ backgroundColor: 'green', color: 'white', padding: 8 }}>
        Connect
      </button>
    </div>
  );
}
```

**Your React Native Version:**
```jsx
import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

function ProfileCard() {
  return (
    // Write your JSX here



  );
}

const styles = StyleSheet.create({
  // Write your styles here



});
```

---

### Exercise 5.3 — Explain the Differences
In your notes, the differences between Regular React and React Native are listed. Using that table, answer:

**Q1. Why does React Native use `<TouchableOpacity>` instead of `<button>`?**

_Answer:_ ___________________________________________________________________________

**Q2. Why does React Native use `StyleSheet.create({})` instead of CSS files?**

_Answer:_ ___________________________________________________________________________

---

## Section 6 — Part 3: What Is Tailwind CSS?

### Exercise 6.1 — Decode the Class Names
Write what each Tailwind class does in plain English.

| Tailwind Class | What It Does |
|----------------|-------------|
| `bg-blue-500` | |
| `p-4` | |
| `text-white` | |
| `rounded-xl` | |
| `font-bold` | |
| `text-center` | |
| `w-full` | |
| `shadow-md` | |
| `border` | |
| `italic` | |

---

### Exercise 6.2 — Build the Style
Write the `className` string using only Tailwind classes to achieve each result. You may combine as many classes as needed.

1. **Dark blue background, white bold text, fully rounded corners (pill shape):**

   `className="` ___________________________________________________________________`"`

2. **Light gray card with padding on all sides, a medium shadow, and slightly rounded corners:**

   `className="` ___________________________________________________________________`"`

3. **Extra-large bold text, centered, with a red color:**

   `className="` ___________________________________________________________________`"`

---

### Exercise 6.3 — Pattern Recognition
What pattern do each of these pairs share? Write the rule you notice.

| Pair | Pattern You See |
|------|----------------|
| `p-2` / `p-8` | |
| `bg-red-300` / `bg-red-700` | |
| `px-4` / `py-4` | |
| `text-sm` / `text-3xl` | |
| `mt-4` / `mb-4` | |

---

### Exercise 6.4 — Compare Approaches
Look at this regular CSS and its Tailwind equivalent.

```css
/* Regular CSS */
.card {
  background-color: blue;
  padding: 16px;
  border-radius: 8px;
}
```

```jsx
{/* Tailwind equivalent */}
<div className="bg-blue-500 p-4 rounded-lg">...</div>
```

**Q. What is one advantage of the Tailwind approach? What might be one disadvantage?**

_Advantage:_ ________________________________________________________________________

_Disadvantage:_ _____________________________________________________________________

---

## Section 7 — Understanding Tailwind Class Names

### Exercise 7.1 — Spacing Classes
Match each spacing class to what it applies padding or margin to.

| Class | What it targets |
|-------|----------------|
| `p-4` | |
| `px-4` | |
| `py-4` | |
| `pt-4` | |
| `pb-4` | |
| `m-4` | |
| `mx-auto` | |
| `mt-6` | |

---

### Exercise 7.2 — Colors & Shades
Answer these questions about Tailwind's color system.

1. In `bg-red-500`, what does the number `500` represent? ______________________________
2. Would `bg-red-100` be lighter or darker than `bg-red-900`? ______________________________
3. Write the class for a medium green text color: ______________________________
4. Write the class for a very light gray background: ______________________________

---

### Exercise 7.3 — Flexbox Layout
Using Tailwind flex classes, write the `className` for each layout requirement.

1. **Children sitting side by side (horizontal):** `className="` ___________________`"`
2. **Children stacked vertically, centered in the middle of the screen:** `className="` ___________________`"`
3. **Children spread apart with equal space between them:** `className="` ___________________`"`
4. **A row where children are vertically centered and have a gap of 16px between them:** `className="` ___________________`"`

---

## Section 8 — Part 4: Tailwind in React Native with NativeWind

### Exercise 8.1 — True or False

| # | Statement | Your Answer |
|---|-----------|-------------|
| 1 | Tailwind CSS works in React Native out of the box. | |
| 2 | NativeWind is a package that brings Tailwind into React Native. | |
| 3 | After installing NativeWind, you can use `className` in React Native. | |
| 4 | NativeWind replaces React Native components like `View` and `Text`. | |
| 5 | NativeWind acts like a bridge between Tailwind and React Native. | |

---

### Exercise 8.2 — Why NativeWind?
Answer in your own words.

**Q1. Why doesn't regular Tailwind CSS work in React Native on its own?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

**Q2. What problem does NativeWind solve for developers who already know Tailwind?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

---

## Section 9 — Setting Up NativeWind

### Exercise 9.1 — Put the Steps in Order
The setup steps below are shuffled. Write the numbers 1–5 to show the correct order.

| Step | Correct Order |
|------|--------------|
| Update `babel.config.js` and add `"nativewind/babel"` as a plugin. | |
| Run `npx tailwindcss init` to create a Tailwind config file. | |
| Run `npm install nativewind` and `npm install --save-dev tailwindcss`. | |
| Create a new Expo app using `npx create-expo-app MyApp`. | |
| Edit `tailwind.config.js` to list your source files in the `content` array. | |

---

### Exercise 9.2 — Config File Questions
Use this `tailwind.config.js` to answer the questions below.

```js
module.exports = {
  content: ["./App.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {},
  },
  plugins: [],
}
```

1. What does the `content` array tell Tailwind? ________________________________________

   ___________________________________________________________________________________

2. What does `**/*.{js,jsx,ts,tsx}` mean in the path? ___________________________________

   ___________________________________________________________________________________

3. Why do we add `"nativewind/babel"` to `babel.config.js`? _____________________________

   ___________________________________________________________________________________

4. What is Babel, in simple terms? ____________________________________________________

---

### Exercise 9.3 — Terminal Commands
Write the correct terminal command for each task.

| Task | Command |
|------|---------|
| Create a new Expo project called `SchoolApp` | |
| Install NativeWind | |
| Install Tailwind CSS as a dev dependency | |
| Generate the Tailwind config file | |

---

## Section 10 — Part 5: React Native with NativeWind

### Exercise 10.1 — Three-Way Comparison
Match each snippet (A, B, C) to its correct method label. Write the letter.

**Snippet A:**
```jsx
<div className="p-4 bg-gray-200 rounded-xl">
  <h1 className="text-xl font-bold text-gray-800">Hello</h1>
</div>
```

**Snippet B:**
```jsx
<View className="p-4 bg-gray-200 rounded-xl">
  <Text className="text-xl font-bold text-gray-800">Hello</Text>
</View>
```

**Snippet C:**
```jsx
const styles = StyleSheet.create({ card: { padding: 16, backgroundColor: '#E5E7EB' } });
<View style={styles.card}>
  <Text style={{ fontSize: 20, fontWeight: 'bold' }}>Hello</Text>
</View>
```

| Method | Snippet Letter |
|--------|---------------|
| React Native with StyleSheet (Traditional) | |
| Regular React (Web) with Tailwind | |
| React Native with NativeWind | |

---

### Exercise 10.2 — Styling Migration
Convert this traditional `StyleSheet` component to use NativeWind `className` only. Keep the same visual result.

**Original:**
```jsx
import { View, Text, StyleSheet } from 'react-native';

function Badge({ label }) {
  return (
    <View style={styles.badge}>
      <Text style={styles.text}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  badge: {
    backgroundColor: '#3B82F6',
    paddingHorizontal: 12,
    paddingVertical: 4,
    borderRadius: 9999,
  },
  text: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 12,
  },
});
```

**Your NativeWind Version:**
```jsx
import { View, Text } from 'react-native';

function Badge({ label }) {
  return (
    <View className="_______________________________________________">
      <Text className="_______________________________________________">{label}</Text>
    </View>
  );
}
```

---

### Exercise 10.3 — Spot the Key Difference
Look at Method 2 (Web React + Tailwind) and Method 3 (React Native + NativeWind) from your notes.

**Q. They look almost identical. What is the only real difference between them?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

---

## Section 11 — Real World Example: A Login Screen

### Exercise 11.1 — Component Identification
Use the login screen code from your notes and answer each question.

1. What component wraps the whole screen and protects against the phone's notch? ______________________
2. What hook stores the email and password values? ______________________
3. What prop on `TextInput` hides characters as dots? ______________________
4. What prop replaces `onClick` in React Native? ______________________
5. What does `keyboardType="email-address"` do? ______________________
6. What does `flex-1` do when applied to the outermost `View`? ______________________

---

### Exercise 11.2 — Add a Feature
The login screen from your notes has email and password fields, and a Sign In button. 

**Your task:** Add a "Forgot Password?" text link below the Sign In button. It should be centered, use small gray text, and print `"Forgot password tapped"` to the console when pressed.

```jsx
{/* Add your Forgot Password link below the Sign In button */}


```

---

### Exercise 11.3 — Explain the Props
Write a plain-English explanation for each prop used in the login screen's TextInput.

| Prop | What It Does |
|------|-------------|
| `value={email}` | |
| `onChangeText={setEmail}` | |
| `secureTextEntry` | |
| `placeholder="you@example.com"` | |
| `keyboardType="email-address"` | |

---

## Section 12 — Conditional Styling

### Exercise 12.1 — Reading Code
Study this component and answer the questions below.

```jsx
function SelectableButton() {
  const [selected, setSelected] = useState(false);

  return (
    <TouchableOpacity
      onPress={() => setSelected(!selected)}
      className={`px-6 py-3 rounded-full ${selected ? 'bg-green-500' : 'bg-gray-200'}`}
    >
      <Text className={`font-semibold ${selected ? 'text-white' : 'text-gray-700'}`}>
        {selected ? 'Selected' : 'Tap to Select'}
      </Text>
    </TouchableOpacity>
  );
}
```

1. What is the starting value of `selected`? ______________________
2. What does `!selected` mean? ______________________
3. What background shows when `selected` is `false`? ______________________
4. What background shows when `selected` is `true`? ______________________
5. What is a template literal, and why is it used here? ______________________

---

### Exercise 12.2 — Ternary Operator Practice
Write the ternary expression for each scenario.

1. If `isLoggedIn` is true, show `"Welcome back!"`, otherwise show `"Please log in."`:

   ```js
   {isLoggedIn ? __________________ : __________________}
   ```

2. If `isActive` is true, apply `bg-blue-500`, otherwise `bg-gray-300`:

   ```jsx
   className={`py-2 px-4 ${__________________________________________}`}
   ```

3. If `hasError` is true, apply `border-red-500`, otherwise `border-gray-300`:

   ```jsx
   className={`border rounded-lg ${__________________________________________}`}
   ```

---

### Exercise 12.3 — Build It: Toggle Button
Write a component called `LikeButton`. It should:
- Start with the label **"Like"** on a gray background.
- When tapped, change to **"Liked ❤️"** on a red background with white text.
- Use `useState` and conditional `className`.

```jsx
import { TouchableOpacity, Text } from 'react-native';
import { useState } from 'react';

function LikeButton() {
  // Write your code here




}
```

---

## Section 13 — FlatList: Displaying Lists of Data

### Exercise 13.1 — FlatList Props Quiz

| Description | Prop Name |
|-------------|-----------|
| The array of items you want to display. | |
| A function that returns a unique ID string for each item. | |
| A function that returns the JSX to render for each item. | |

---

### Exercise 13.2 — Reading FlatList Code
Study the contacts list from your notes and answer these questions.

1. What does `keyExtractor={(item) => item.id}` do and why is it needed? ______________________

   ___________________________________________________________________________________

2. What does `name[0]` return if `name` is `"Alice Johnson"`? ______________________

3. What is the purpose of the `ContactItem` function? ______________________

   ___________________________________________________________________________________

4. Why is `FlatList` better than using `.map()` for a list of 500 contacts? ______________________

   ___________________________________________________________________________________

---

### Exercise 13.3 — Build a FlatList
You have this data:

```js
const fruits = [
  { id: '1', name: 'Mango', color: 'Yellow' },
  { id: '2', name: 'Apple', color: 'Red' },
  { id: '3', name: 'Grape', color: 'Purple' },
];
```

Write a complete component called `FruitList` that renders this data using `FlatList`. Each item should show the fruit's name in bold and its color in gray text below it.

```jsx
import { View, Text, FlatList } from 'react-native';

const fruits = [
  { id: '1', name: 'Mango', color: 'Yellow' },
  { id: '2', name: 'Apple', color: 'Red' },
  { id: '3', name: 'Grape', color: 'Purple' },
];

function FruitItem({ name, color }) {
  return (
    // Write FruitItem JSX here



  );
}

export default function FruitList() {
  return (
    // Write FlatList here



  );
}
```

---

## Section 14 — ScrollView vs FlatList

### Exercise 14.1 — Choose the Right Component
For each scenario, write whether you would use `ScrollView` or `FlatList`, and explain why in one sentence.

| Scenario | ScrollView or FlatList? | Why? |
|----------|------------------------|------|
| A profile screen with a photo, bio, stats, and a few buttons. | | |
| A social media feed with hundreds of posts from an API. | | |
| An "About" page with paragraphs of text and images. | | |
| A list of 200 product items from a database. | | |
| A checkout summary screen with 5–6 line items. | | |

---

### Exercise 14.2 — Short Answer

**Q1. What does "FlatList only renders items currently visible on screen" mean? Why does this matter?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

**Q2. What does "ScrollView renders everything at once" mean as a drawback?**

_Answer:_ ___________________________________________________________________________

___________________________________________________________________________________

---

### Exercise 14.3 — Rewrite with ScrollView
Take the `ProfileScreen` from your notes and add two more sections to it: a "Recent Activity" heading and a "Settings" heading, each followed by a short paragraph. Keep it wrapped in `ScrollView`.

```jsx
import { ScrollView, View, Text } from 'react-native';

function ProfileScreen() {
  return (
    <ScrollView className="flex-1 bg-white px-4">
      <Text className="text-3xl font-bold mt-6">Profile</Text>
      <View className="h-40 bg-gray-200 rounded-xl mt-4" />
      <Text className="text-lg mt-4 text-gray-700">Bio information here...</Text>

      {/* Add Recent Activity section here */}


      {/* Add Settings section here */}


    </ScrollView>
  );
}
```

---

## Section 15 — Navigation Between Screens

### Exercise 15.1 — Navigation Terminology
Match each term to its correct description.

| Term | Description |
|------|-------------|
| `NavigationContainer` | |
| `Stack.Navigator` | |
| `Stack.Screen` | |
| `navigation.navigate('Profile')` | |
| `navigation.goBack()` | |
| `route.params` | |
| `initialRouteName` | |

**Descriptions:**
- **A** — Goes back to the previous screen.
- **B** — Where data passed via `navigate()` is accessed in the receiving screen.
- **C** — Registers a screen with a name and component inside the navigator.
- **D** — Wraps the entire app — required for navigation to work.
- **E** — Navigates to the screen named "Profile".
- **F** — Sets which screen opens first when the app starts.
- **G** — Manages a stack of screens, like a deck of cards you push on and pop off.

---

### Exercise 15.2 — Passing Data Between Screens
Complete the missing code.

**HomeScreen — Send the data:**
```jsx
// Send name: 'Daniel' and age: 17 to the Profile screen
navigation.navigate('Profile', {
  _______________: '_______________',
  _______________: _______________,
});
```

**ProfileScreen — Receive and display it:**
```jsx
export default function ProfileScreen({ _______________ }) {
  const { name, age } = _______________.params;

  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-2xl font-bold">{_______________}</Text>
      <Text className="text-lg text-gray-600">Age: {_______________}</Text>
    </View>
  );
}
```

---

### Exercise 15.3 — Build a Two-Screen App
Write the `App.js`, `HomeScreen.jsx`, and `AboutScreen.jsx` for a simple two-screen app.

- `HomeScreen` should have a title "Home" and a button "Go to About".
- `AboutScreen` should have a title "About" and a "Go Back" button.
- Style everything with NativeWind.

```jsx
// App.js
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    // Write your NavigationContainer and Stack here



  );
}
```

```jsx
// HomeScreen.jsx
export default function HomeScreen({ navigation }) {
  return (
    // Write your HomeScreen JSX here



  );
}
```

```jsx
// AboutScreen.jsx
export default function AboutScreen({ navigation }) {
  return (
    // Write your AboutScreen JSX here



  );
}
```

---

## Section 16 — Custom Components & Common Mistakes

### Exercise 16.1 — Custom Component Analysis
Study the reusable `Button` component from your notes and answer the questions.

```jsx
export default function Button({ label, onPress, color = 'bg-blue-500', textColor = 'text-white' }) {
  return (
    <TouchableOpacity
      onPress={onPress}
      className={`${color} py-3 px-6 rounded-xl items-center`}
    >
      <Text className={`${textColor} font-bold text-base`}>{label}</Text>
    </TouchableOpacity>
  );
}
```

1. What does `color = 'bg-blue-500'` mean in the parameter list? ______________________

   ___________________________________________________________________________________

2. Write the JSX to render a **green** button with label `"Save"`: ______________________

3. Write the JSX to render a **red** button with label `"Delete"`: ______________________

4. What happens if you use `<Button label="Go" onPress={() => {}} />` without a `color` prop? ______________________

---

### Exercise 16.2 — Spot & Fix the Bug
Each snippet has a common beginner mistake. Identify and correct it.

**Bug 1:**
```jsx
<View>Hello World</View>
```
_Problem:_ __________________________________________________________________________

_Fixed:_
```jsx

```

**Bug 2:**
```jsx
import { View, Text } from 'react-native';
<Image source={require('./photo.png')} />
```
_Problem:_ __________________________________________________________________________

_Fixed:_
```jsx

```

**Bug 3:**
```jsx
<View className="bg-white">
  <Text>This screen looks empty!</Text>
</View>
```
_Problem:_ __________________________________________________________________________

_Fixed:_
```jsx

```

**Bug 4:**
```jsx
<div className="p-4">
  <h1>Welcome</h1>
  <button>Press Me</button>
</div>
```
_Problem:_ __________________________________________________________________________

_Fixed:_
```jsx

```

---

### Exercise 16.3 — Quick Reference Quiz
Without looking at your notes, name the React Native component for each task.

| Task | Component |
|------|-----------|
| Display any text on screen | |
| A scrollable container for large lists | |
| A tappable element (for buttons) | |
| An on/off toggle switch | |
| A spinning loading indicator | |
| A popup over the current screen | |
| A container that respects the phone's safe areas | |
| Where users type text | |
| Display an image | |
| A scrollable container for mixed content | |

---

### Exercise 16.4 — Final Challenge: Profile Card

> **Challenge:** Using React Native and NativeWind, build a `ProfileCard` component with ALL of the following:
> 1. A circular avatar showing the first letter of the name (e.g. "D" for "Daniel").
> 2. The user's full name in bold large text.
> 3. Their job title in smaller gray text.
> 4. A **Follow** button that toggles to **Unfollow** when tapped — use `useState`.
> 5. Style everything using NativeWind `className` only — no `StyleSheet.create()`.

```jsx
import { View, Text, TouchableOpacity } from 'react-native';
import { useState } from 'react';

export default function ProfileCard({ name, jobTitle }) {
  // Write your code here




}
```

**Bonus:** Add a second piece of state called `followerCount` that starts at `128`. When the Follow button is tapped, increase it by 1. When Unfollow is tapped, decrease it by 1. Display the count below the button.

```jsx
{/* Bonus follower count display */}

```

---

## Bonus Challenge — Build a Full Mini App

> **Instructions:** Using everything you have learned, build a mini contacts app with the following screens:
>
> - **HomeScreen:** A title "My Contacts" and a `FlatList` showing at least 4 contacts (name + role).
> - **DetailScreen:** When a contact is tapped on HomeScreen, navigate to DetailScreen and display their full name and role using `route.params`.
> - Use NativeWind for all styling.
> - Create a reusable `ContactCard` component for each list item.

Sketch your component structure here before coding:

```
App.js
├── HomeScreen
│   └── ContactCard (reusable)
└── DetailScreen
```

_Notes / Planning:_

___________________________________________________________________________________

___________________________________________________________________________________

___________________________________________________________________________________

---

*The best way to truly learn this is to build things. Take these examples, modify them, add features, and break things on purpose — then fix them. That is how real developers learn.*

---

**End of Workbook**
