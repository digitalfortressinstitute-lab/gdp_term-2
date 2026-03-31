#  React Native + Tailwind (NativeWind) — Complete Beginner Guide

---

#  Introduction

React Native allows you to build mobile apps using JavaScript — the same language used for websites.

Instead of writing separate apps for Android and iOS, you write one codebase that works on both.

To make styling easier, we use **NativeWind**, which brings **Tailwind CSS** into React Native.

---

#  Core Idea

Think of it like this:

* React = websites (browser)
* React Native = mobile apps (phone)
* Tailwind = styling system
* NativeWind = Tailwind for mobile

---

#  Lesson 1 — Components (Building Blocks)

##  Concept

Everything in React Native is a **component**.

Think of components like LEGO blocks:

* A button = component
* A card = component
* A whole screen = component

You combine them to build your app.

---

##  Example

```jsx
import { View, Text } from 'react-native';

export default function Greeting() {
  return (
    <View className="p-4">
      <Text className="text-lg">Hello Student 👋</Text>
    </View>
  );
}
```

---

##  Breakdown

* `View` → container (like a box)
* `Text` → displays text
* `className` → Tailwind styling

---

##  Exercise

Create a component that shows:

* Your name
* Your favorite tech skill

---

#  Lesson 2 — Props (Making Components Dynamic)

##  Concept

Props allow you to pass data into a component.

👉 Without props → static
👉 With props → reusable and dynamic

---

##  Example

```jsx
function UserCard({ name }) {
  return <Text>Hello {name}</Text>;
}
```

---

##  Breakdown

* `{name}` is passed into the component
* You can reuse this component with different values

---

##  Exercise

Render the component 3 times with different names.

---

#  Lesson 3 — State (Making Things Interactive)

##  Concept

State is data that can change over time.

When state changes → UI updates automatically

---

##  Example

```jsx
import { useState } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

export default function Counter() {
  const [count, setCount] = useState(0);

  return (
    <View className="items-center mt-10">
      <Text className="text-xl">{count}</Text>

      <TouchableOpacity
        className="bg-blue-500 px-4 py-2 mt-4 rounded"
        onPress={() => setCount(count + 1)}
      >
        <Text className="text-white">Increase</Text>
      </TouchableOpacity>
    </View>
  );
}
```

---

##  Breakdown

* `useState(0)` → initial value = 0
* `setCount` → updates state
* `onPress` → runs when button is tapped

---

##  Exercise

Modify the counter to:

* Add a “Decrease” button

---

#  Lesson 4 — Styling with Tailwind (NativeWind)

##  Concept

Instead of writing long styles, Tailwind uses small utility classes.

Example:

* `p-4` → padding
* `bg-blue-500` → background color

---

##  Example

```jsx
<View className="bg-gray-100 p-5 rounded-xl">
  <Text className="text-xl font-bold">Styled Card</Text>
</View>
```

---

##  Breakdown

* `bg-gray-100` → background color
* `p-5` → padding
* `rounded-xl` → rounded corners

---

##  Exercise

Create a card with:

* Blue background
* White text
* Rounded edges

---

#  Lesson 5 — Forms (User Input)

##  Concept

Forms allow users to input data (email, password, etc.)

---

##  Example

```jsx
import { useState } from 'react';
import { TextInput, View } from 'react-native';

export default function Form() {
  const [email, setEmail] = useState('');

  return (
    <View className="p-4">
      <TextInput
        className="border p-3 rounded-lg"
        placeholder="Enter email"
        value={email}
        onChangeText={setEmail}
      />
    </View>
  );
}
```

---

##  Breakdown

* `TextInput` → input field
* `value` → current state
* `onChangeText` → updates state

---

##  Exercise

Add:

* Password input
* Button below it

---

#  Lesson 6 — Buttons & Interaction

##  Concept

Users interact with apps through buttons.

---

##  Example

```jsx
<TouchableOpacity className="bg-blue-500 py-3 rounded-lg">
  <Text className="text-white text-center">Click Me</Text>
</TouchableOpacity>
```

---

##  Breakdown

* `TouchableOpacity` → pressable element
* `onPress` → handles click

---

##  Exercise

Create a button that logs “Hello” when pressed.

---

#  Lesson 7 — Lists (FlatList)

##  Concept

Use `FlatList` to display lists efficiently.

---

##  Example

```jsx
import { FlatList, Text } from 'react-native';

const data = [
  { id: '1', name: 'Alice' },
  { id: '2', name: 'Bob' },
];

export default function List() {
  return (
    <FlatList
      data={data}
      keyExtractor={(item) => item.id}
      renderItem={({ item }) => (
        <Text className="p-4">{item.name}</Text>
      )}
    />
  );
}
```

---

##  Breakdown

* `data` → array of items
* `renderItem` → how each item looks
* `keyExtractor` → unique ID

---

##  Exercise

Display a list of:

* 5 students
* Each with a role

---

#  Lesson 8 — Navigation (MULTI-SCREEN APPS)

##  Concept

Apps have multiple screens. Navigation lets users move between them.

---

##  Example

```jsx
navigation.navigate('Profile');
```

---

##  Breakdown

* Moves user to another screen
* Works using React Navigation

---

##  Exercise

Create:

* Home screen
* Profile screen
* Button to switch screens

---

#  Lesson 9 — Conditional Styling

##  Concept

UI can change based on state.

---

##  Example

```jsx
className={`p-4 ${active ? 'bg-green-500' : 'bg-gray-300'}`}
```

---

##  Breakdown

* Ternary operator changes styles dynamically

---

##  Exercise

Create a button that:

* Changes color when clicked

---

#  Lesson 10 — Reusable Components

##  Concept

Write once → use everywhere

---

##  Example

```jsx
export default function Button({ label }) {
  return (
    <TouchableOpacity className="bg-blue-500 p-3 rounded">
      <Text className="text-white text-center">{label}</Text>
    </TouchableOpacity>
  );
}
```

---

##  Exercise

Use your button component in 3 places.

---

#  Advanced (Industry-Level Basics)

## SafeAreaView

Prevents UI from overlapping phone notch.

---

## Loading States

```jsx
<ActivityIndicator size="large" />
```

---

## Folder Structure

```bash
src/
 ├── components/
 ├── screens/
 ├── navigation/
```

---

#  Common Mistakes

* Not using `<Text>`
* Using HTML tags
* Forgetting imports
* Not using `flex-1`

---


#  Final Project

👉 Build a **Student Dashboard App** with:

* Login screen
* List of students
* Profile screen
