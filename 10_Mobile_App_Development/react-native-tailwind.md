React Native & Tailwind CSS — Complete Beginner Notes
Written for students who are just getting started
 
First Things First — What Even Is React Native?
Okay, let's start from scratch. You probably already know what a website is — it's something you visit in a browser like Chrome or Safari. But what about apps on your phone? Like Instagram, TikTok, or WhatsApp? Those are mobile apps, and they live on your phone, not in a browser.
React Native is a tool that lets you build those kinds of mobile apps using JavaScript — the same programming language used for websites.
Here's the cool part: with React Native, you write your code once, and it works on both Android phones and iPhones (iOS). You don't need to learn two separate languages. That's a huge deal.
 
What Is Regular React? (And How Is It Different?)
Before React Native, there was just React. React is a JavaScript library created by Facebook/Meta for building websites and web apps — things that run in your browser.
Think of it like this:
React = building things for the web (browser)
React Native = building things for mobile phones (Android + iOS)
They look very similar in code, but there are some important differences. We'll go through those shortly.
 
Key Words You Need To Know (Glossary)
Before we dive in, let's define some important words you'll keep seeing:
Component — A piece of your app's screen. Think of it like a LEGO brick. Your whole app is made of many components put together. A button is a component. A text box is a component. A whole screen can be a component.
JSX — A special way of writing code that looks like HTML but is actually JavaScript. It lets you describe what your screen should look like right inside your code. Example: <Text>Hello!</Text>
Props — Short for "properties." These are like settings you pass into a component to customize it. For example, you can pass a color or a label to a button component.
State — Information that your app remembers and can change over time. For example, if a user clicks a button 5 times, the number 5 is "state." When state changes, the screen automatically updates.
Styling — How you make things look pretty — colors, sizes, spacing, fonts, etc.
View — In React Native, a View is like a div in HTML. It's a box that holds other things.
Text — In React Native, all words on screen must be inside a <Text> component. You can't just type words freely like in HTML.
StyleSheet — React Native's built-in way to write styles (like CSS, but in JavaScript).
NativeWind — A library that brings Tailwind CSS into React Native. We'll explain this fully later.
className — In regular React (web), you use className to apply CSS styles to elements.
style — In React Native, you use style instead of className to apply styles.
 
Part 1 — Regular React (For Websites)
Let's look at how you'd build something in regular React first, so we can compare it to React Native later.
In regular React, you're building for the browser. You use HTML-like elements such as div, p, h1, button, img, etc.
Here's a simple example — a card showing a user's name:
jsx// Regular React (for websites)

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
What's happening here?
We have a div (a box), inside it is a heading h1, a paragraph p, and a button. Each element has styles applied directly using the style attribute. This works fine for websites.
 
Part 2 — React Native (For Mobile Phones)
Now here's the same card, but written in React Native:
jsx// React Native (for mobile phones)

import { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

function UserCard() {
  return (
    <View style={styles.card}>
      <Text style={styles.name}>John Doe</Text>
      <Text style={styles.job}>Software Developer</Text>
      <TouchableOpacity style={styles.button}>
        <Text style={styles.buttonText}>Follow</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    padding: 20,
    backgroundColor: '#f0f0f0',
    borderRadius: 10,
  },
  name: {
    fontSize: 24,
    color: '#333',
  },
  job: {
    fontSize: 16,
    color: '#666',
  },
  button: {
    backgroundColor: 'blue',
    padding: 10,
    borderRadius: 5,
  },
  buttonText: {
    color: 'white',
    textAlign: 'center',
  },
});
What changed?
Let's go through it one by one:
div became View — React Native doesn't use HTML tags. A View is the mobile version of a div.
h1 and p became Text — All text on screen must be wrapped in <Text>. There's no h1, p, or span in React Native.
button became TouchableOpacity — React Native uses TouchableOpacity for buttons because it gives a nice fade effect when you tap it. There's also Pressable and TouchableHighlight which do similar things.
StyleSheet.create({}) — Instead of writing styles inline or in a CSS file, React Native uses StyleSheet.create() to organize styles. It's like CSS but written in JavaScript objects.
No className — In regular React for the web, you'd write className="card" and put the styles in a separate CSS file. In React Native, there are no CSS files. Everything lives in the JavaScript file using style={styles.someName}.
 
The Big Differences — Side By Side
Here's a quick comparison table so the differences are crystal clear:
FeatureRegular React (Web)React Native (Mobile)Box/Container<div><View>Text<p>, <h1>, <span><Text>Button<button><TouchableOpacity> or <Pressable>Image<img src="..." /><Image source={require('./img.png')} />Input<input type="text" /><TextInput />StylingCSS files or classNameStyleSheet.create() or inline style={}ScrollingCSS overflow or browser default<ScrollView> componentListmap() + HTML tags<FlatList> component
 
Part 3 — What Is Tailwind CSS?
Now let's talk about Tailwind CSS. This is a very popular styling tool.
Normally when you style something, you'd write custom CSS like this:
css.card {
  background-color: blue;
  padding: 16px;
  border-radius: 8px;
}
Tailwind takes a different approach. Instead of writing your own CSS, Tailwind gives you tiny pre-made class names that you just apply directly in your code. Each class name does one specific thing.
For example:
bg-blue-500 = sets background color to a specific shade of blue
p-4 = adds padding of 16px on all sides
rounded-lg = adds large border radius (rounded corners)
text-white = sets text color to white
text-xl = sets font size to extra large
So instead of writing CSS, you just combine these class names:
jsx// Regular React with Tailwind CSS (web)

function UserCard() {
  return (
    <div className="bg-gray-100 p-5 rounded-xl">
      <h1 className="text-2xl font-bold text-gray-800">John Doe</h1>
      <p className="text-base text-gray-500">Software Developer</p>
      <button className="bg-blue-500 text-white px-5 py-2 rounded-md">
        Follow
      </button>
    </div>
  );
}
See how clean that is? No separate CSS file, no StyleSheet — just small descriptive class names right in your JSX. This is what makes Tailwind so popular.
 
Understanding Tailwind Class Names — How Do They Work?
Tailwind class names follow a pattern. Once you learn the pattern, you can guess most class names without even looking them up.
Colors:
bg-red-500 = red background (500 is the shade, from 100 light to 900 dark)
text-blue-700 = dark blue text
border-green-300 = light green border
Spacing (padding and margin):
p-4 = padding on all 4 sides (16px)
px-4 = padding left and right only (x = horizontal)
py-4 = padding top and bottom only (y = vertical)
pt-4 = padding top only
m-4 = margin on all 4 sides
mx-auto = margin left and right set to auto (centers something horizontally)
Sizing:
w-full = width 100% of parent
w-1/2 = width 50%
h-10 = height of 40px
max-w-md = maximum width medium (good for cards)
Typography (text):
text-sm = small text
text-base = normal text size
text-lg = large text
text-xl = extra large
text-2xl, text-3xl etc = getting bigger
font-bold = bold text
font-medium = medium weight text
text-center = center aligned text
text-left = left aligned
italic = italic text
Flexbox (layout):
flex = turns on flex layout
flex-row = children line up horizontally (side by side)
flex-col = children stack vertically (top to bottom)
items-center = vertically center children
justify-center = horizontally center children
justify-between = spread children with space between them
gap-4 = space of 16px between children
Borders:
border = adds a thin border
border-2 = slightly thicker border
rounded = slight rounded corners
rounded-lg = more rounded
rounded-full = completely round (circle if same width and height)
Shadows:
shadow = small shadow
shadow-md = medium shadow
shadow-lg = large shadow
 
Part 4 — Tailwind In React Native with NativeWind
Here's the catch: Tailwind CSS was originally made for the web. React Native apps are not web apps, so Tailwind doesn't work in React Native out of the box.
That's where NativeWind comes in.
NativeWind is a package that brings Tailwind's class name system into React Native. It lets you use className on your React Native components just like you would in regular React web development.
It's like a bridge between Tailwind and React Native.
 
Setting Up NativeWind (Step By Step)
Step 1 — Create a new Expo app
Expo is the easiest way to start a React Native project. In your terminal, run:
bashnpx create-expo-app MyApp
cd MyApp
Step 2 — Install NativeWind and Tailwind
bashnpm install nativewind
npm install --save-dev tailwindcss
Step 3 — Set up Tailwind config
Run this to create a Tailwind config file:
bashnpx tailwindcss init
Then open the tailwind.config.js file that was created and update it:
js// tailwind.config.js

module.exports = {
  content: ["./App.{js,jsx,ts,tsx}", "./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {},
  },
  plugins: [],
}
The content line tells Tailwind where your code files are so it knows which class names to include.
Step 4 — Update babel.config.js
Open babel.config.js and add "nativewind/babel" as a plugin:
js// babel.config.js

module.exports = function (api) {
  api.cache(true);
  return {
    presets: ['babel-preset-expo'],
    plugins: ["nativewind/babel"],  // add this line
  };
};
Babel is a tool that processes your code before it runs. Adding NativeWind here lets it understand Tailwind class names.
Now you're ready to use Tailwind in React Native!
 
Part 5 — React Native With NativeWind (The Good Stuff)
Here's our same UserCard example, now written using NativeWind in React Native:
jsx// React Native with NativeWind (Tailwind)

import { View, Text, TouchableOpacity } from 'react-native';

function UserCard() {
  return (
    <View className="bg-gray-100 p-5 rounded-xl">
      <Text className="text-2xl font-bold text-gray-800">John Doe</Text>
      <Text className="text-base text-gray-500">Software Developer</Text>
      <TouchableOpacity className="bg-blue-500 px-5 py-2 rounded-md mt-3">
        <Text className="text-white text-center font-semibold">Follow</Text>
      </TouchableOpacity>
    </View>
  );
}
Look at how clean that is! No StyleSheet.create() at the bottom. No separate styles object. Just className with Tailwind class names — just like in regular web React.
 
Big Comparison — All Three Approaches Side By Side
Let's look at all three methods for the same card — regular React native styles, web React with Tailwind, and React Native with NativeWind:
Method 1: React Native with StyleSheet (Traditional)
jsximport { View, Text, TouchableOpacity, StyleSheet } from 'react-native';

function Card() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Hello World</Text>
      <TouchableOpacity style={styles.button}>
        <Text style={styles.buttonText}>Click Me</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: 16,
    backgroundColor: '#e5e7eb',
    borderRadius: 12,
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#1f2937',
    marginBottom: 12,
  },
  button: {
    backgroundColor: '#3b82f6',
    paddingVertical: 10,
    paddingHorizontal: 20,
    borderRadius: 8,
  },
  buttonText: {
    color: 'white',
    textAlign: 'center',
  },
});
Method 2: Regular React (Web) with Tailwind
jsxfunction Card() {
  return (
    <div className="p-4 bg-gray-200 rounded-xl">
      <h1 className="text-xl font-bold text-gray-800 mb-3">Hello World</h1>
      <button className="bg-blue-500 py-2 px-5 rounded-lg text-white">
        Click Me
      </button>
    </div>
  );
}
Method 3: React Native with NativeWind (Tailwind)
jsximport { View, Text, TouchableOpacity } from 'react-native';

function Card() {
  return (
    <View className="p-4 bg-gray-200 rounded-xl">
      <Text className="text-xl font-bold text-gray-800 mb-3">Hello World</Text>
      <TouchableOpacity className="bg-blue-500 py-2 px-5 rounded-lg">
        <Text className="text-white text-center">Click Me</Text>
      </TouchableOpacity>
    </View>
  );
}
Notice how Method 2 (web React + Tailwind) and Method 3 (React Native + NativeWind) look almost identical. The only difference is the component names — div vs View, h1 vs Text, button vs TouchableOpacity. The className styling is the exact same!
This is the beauty of learning NativeWind. If you already know Tailwind from web development, you can jump into React Native styling almost immediately.
 
Real World Example — A Login Screen
Let's build something more complete so you can see how everything works together. Here's a login screen using React Native + NativeWind:
jsximport { View, Text, TextInput, TouchableOpacity, SafeAreaView } from 'react-native';
import { useState } from 'react';

export default function LoginScreen() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  function handleLogin() {
    console.log('Logging in with:', email, password);
  }

  return (
    <SafeAreaView className="flex-1 bg-white">
      <View className="flex-1 px-6 justify-center">

        {/* Title Section */}
        <Text className="text-4xl font-bold text-gray-900 mb-2">
          Welcome Back
        </Text>
        <Text className="text-base text-gray-500 mb-10">
          Sign in to your account
        </Text>

        {/* Email Input */}
        <Text className="text-sm font-medium text-gray-700 mb-1">
          Email Address
        </Text>
        <TextInput
          className="border border-gray-300 rounded-lg px-4 py-3 mb-5 text-base text-gray-800"
          placeholder="you@example.com"
          value={email}
          onChangeText={setEmail}
          keyboardType="email-address"
        />

        {/* Password Input */}
        <Text className="text-sm font-medium text-gray-700 mb-1">
          Password
        </Text>
        <TextInput
          className="border border-gray-300 rounded-lg px-4 py-3 mb-8 text-base text-gray-800"
          placeholder="Enter your password"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
        />

        {/* Login Button */}
        <TouchableOpacity
          className="bg-blue-600 py-4 rounded-xl items-center mb-4"
          onPress={handleLogin}
        >
          <Text className="text-white font-bold text-base">Sign In</Text>
        </TouchableOpacity>

        {/* Sign Up Link */}
        <View className="flex-row justify-center">
          <Text className="text-gray-500">Don't have an account? </Text>
          <TouchableOpacity>
            <Text className="text-blue-600 font-semibold">Sign Up</Text>
          </TouchableOpacity>
        </View>

      </View>
    </SafeAreaView>
  );
}
Let's break down the new things:
SafeAreaView — A special container that makes sure your content doesn't hide behind the phone's status bar or notch at the top. Always use this as your outermost container for screens.
TextInput — The mobile version of <input type="text" /> from HTML. It lets users type text.
value={email} — Links the input to the email state variable.
onChangeText={setEmail} — Every time the user types a character, it updates the email state. onChangeText is React Native's version of the web's onChange.
secureTextEntry — Hides the characters as dots, like a password field. Just adding this prop (no value needed) turns it on.
onPress={handleLogin} — React Native's version of onClick. Called when user taps the button.
keyboardType="email-address" — Tells the phone to show a keyboard optimized for email addresses (with @, .com etc).
flex-1 — Makes the element take up all remaining space. Very commonly used in React Native.
items-center — Centers children horizontally when using flex-col, or vertically when using flex-row.
justify-center — Centers children the opposite way.
 
Conditional Styling — Changing Styles Based On State
Sometimes you want to change how something looks depending on what's happening. For example, a button that turns green when selected.
jsximport { TouchableOpacity, Text } from 'react-native';
import { useState } from 'react';

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
What's happening:
selected ? 'bg-green-500' : 'bg-gray-200' — This is a ternary operator. It means: "if selected is true, use bg-green-500, otherwise use bg-gray-200."
The backticks ` and ${} let you mix regular text with JavaScript expressions inside a string. This is called a template literal.
setSelected(!selected) — Flips the value. If selected was true, it becomes false. If false, it becomes true. The ! means "opposite of."
 
FlatList — Displaying Lists of Data
In React Native, when you have a long list of items (like a feed or contacts), you use FlatList instead of just mapping through an array. FlatList is optimized for performance — it only renders items that are currently visible on screen.
jsximport { View, Text, FlatList, TouchableOpacity } from 'react-native';

const contacts = [
  { id: '1', name: 'Alice Johnson', role: 'Designer' },
  { id: '2', name: 'Bob Smith', role: 'Developer' },
  { id: '3', name: 'Carol White', role: 'Manager' },
  { id: '4', name: 'David Lee', role: 'Developer' },
];

function ContactItem({ name, role }) {
  return (
    <TouchableOpacity className="flex-row items-center bg-white p-4 mb-2 rounded-xl shadow-sm">
      <View className="w-12 h-12 bg-blue-500 rounded-full items-center justify-center mr-4">
        <Text className="text-white text-lg font-bold">{name[0]}</Text>
      </View>
      <View>
        <Text className="text-base font-semibold text-gray-800">{name}</Text>
        <Text className="text-sm text-gray-500">{role}</Text>
      </View>
    </TouchableOpacity>
  );
}

export default function ContactsList() {
  return (
    <View className="flex-1 bg-gray-100 p-4">
      <Text className="text-2xl font-bold text-gray-900 mb-4">Contacts</Text>
      <FlatList
        data={contacts}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <ContactItem name={item.name} role={item.role} />
        )}
      />
    </View>
  );
}
Breaking it down:
data={contacts} — The array of items you want to display.
keyExtractor={(item) => item.id} — A function that gives each item a unique key. React needs this to keep track of which item is which.
renderItem={({ item }) => ...} — A function that says "for each item in the data, render this component." The item is one element from your array.
name[0] — Gets the first character of the name string. So "Alice Johnson" becomes "A". Used here as an avatar placeholder.
 
ScrollView vs FlatList — Which To Use?
ScrollView — Use this when you have a small number of items, or when the content is a mix of different things (not a list). ScrollView renders everything at once.
jsximport { ScrollView, View, Text } from 'react-native';

function ProfileScreen() {
  return (
    <ScrollView className="flex-1 bg-white px-4">
      <Text className="text-3xl font-bold mt-6">Profile</Text>
      <View className="h-40 bg-gray-200 rounded-xl mt-4" />
      <Text className="text-lg mt-4 text-gray-700">Bio information here...</Text>
      {/* more content */}
    </ScrollView>
  );
}
FlatList — Use this when you have a long or unknown number of similar items (like messages, posts, contacts). FlatList only renders what's visible, so it's much faster with large data.
The rule of thumb: short mixed content = ScrollView. Long list of same-type items = FlatList.
 
Navigation Between Screens
Real apps have multiple screens. To move between screens in React Native, you use a library called React Navigation.
Install it:
bashnpm install @react-navigation/native
npm install @react-navigation/native-stack
npm install react-native-screens react-native-safe-area-context
Here's a simple two-screen app:
jsx// App.js

import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from './screens/HomeScreen';
import ProfileScreen from './screens/ProfileScreen';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator initialRouteName="Home">
        <Stack.Screen name="Home" component={HomeScreen} />
        <Stack.Screen name="Profile" component={ProfileScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
jsx// screens/HomeScreen.jsx

import { View, Text, TouchableOpacity } from 'react-native';

export default function HomeScreen({ navigation }) {
  return (
    <View className="flex-1 bg-white items-center justify-center">
      <Text className="text-3xl font-bold text-gray-900 mb-6">Home Screen</Text>
      <TouchableOpacity
        className="bg-blue-500 px-8 py-3 rounded-xl"
        onPress={() => navigation.navigate('Profile')}
      >
        <Text className="text-white font-bold text-base">Go to Profile</Text>
      </TouchableOpacity>
    </View>
  );
}
jsx// screens/ProfileScreen.jsx

import { View, Text, TouchableOpacity } from 'react-native';

export default function ProfileScreen({ navigation }) {
  return (
    <View className="flex-1 bg-white items-center justify-center">
      <Text className="text-3xl font-bold text-gray-900 mb-6">Profile Screen</Text>
      <TouchableOpacity
        className="bg-gray-300 px-8 py-3 rounded-xl"
        onPress={() => navigation.goBack()}
      >
        <Text className="text-gray-800 font-bold text-base">Go Back</Text>
      </TouchableOpacity>
    </View>
  );
}
Key navigation terms:
NavigationContainer — Wraps your entire app. Required for navigation to work.
Stack.Navigator — Manages a stack of screens. Like a stack of cards — you push new screens on top and pop them off to go back.
Stack.Screen — Registers a screen with a name and a component.
navigation.navigate('Profile') — Goes to the screen named 'Profile'.
navigation.goBack() — Goes back to the previous screen.
{ navigation } — Every screen component automatically receives the navigation object as a prop from React Navigation.
 
Passing Data Between Screens
Often you'll want to pass data from one screen to another — like tapping on a post and going to a detail page with that post's info.
jsx// Sending data (from HomeScreen)
navigation.navigate('Profile', { userId: 42, name: 'Alice' });

// Receiving data (in ProfileScreen)
export default function ProfileScreen({ route }) {
  const { userId, name } = route.params;

  return (
    <View className="flex-1 items-center justify-center">
      <Text className="text-2xl font-bold">User #{userId}</Text>
      <Text className="text-lg text-gray-600">{name}</Text>
    </View>
  );
}
route.params — This is where the data you passed with navigate() shows up. route is automatically given to every screen just like navigation.
 
Custom Components — Reusing Your Work
One of the most powerful things in React Native (and React) is making your own reusable components. Instead of writing the same code over and over, you write it once and reuse it everywhere.
jsx// components/Button.jsx

import { TouchableOpacity, Text } from 'react-native';

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
Now you can use this button anywhere:
jsximport Button from './components/Button';

// A blue button (default)
<Button label="Sign In" onPress={() => console.log('signed in')} />

// A red button
<Button label="Delete" onPress={() => console.log('deleted')} color="bg-red-500" />

// A green button
<Button label="Save" onPress={() => console.log('saved')} color="bg-green-500" />
color = 'bg-blue-500' — This is a default prop value. If no color prop is provided when using the component, it automatically uses 'bg-blue-500'.
 
Common Mistakes To Avoid
Forgetting to wrap text in <Text>
In React Native, you cannot put raw text directly in a View. It must always be inside <Text>.
jsx// Wrong
<View>Hello World</View>

// Correct
<View>
  <Text>Hello World</Text>
</View>
Using web HTML tags
There's no div, p, h1, span, img, or button in React Native.
jsx// Wrong (web HTML)
<div className="p-4">
  <h1>Title</h1>
  <button>Click</button>
</div>

// Correct (React Native)
<View className="p-4">
  <Text className="text-2xl font-bold">Title</Text>
  <TouchableOpacity>
    <Text>Click</Text>
  </TouchableOpacity>
</View>
Forgetting flex-1 on the root container
In React Native, elements don't automatically fill the screen. You often need flex-1 on your outermost container.
jsx// Screen might appear empty or tiny
<View className="bg-white">...</View>

// Takes up the full screen
<View className="flex-1 bg-white">...</View>
Not importing components
Every component you use from React Native must be imported.
jsx// Will crash - Image not imported
import { View, Text } from 'react-native';
<Image source={...} />

// Correct
import { View, Text, Image } from 'react-native';
<Image source={...} />
 
Quick Reference — Most Used Components
Here's a cheat sheet of the components you'll use most often:
View — A box/container. The building block of every screen.
Text — Display any text on screen.
TextInput — A field where the user can type.
TouchableOpacity — A pressable element. Use it for buttons and anything tappable. Fades slightly when tapped.
Pressable — A newer, more powerful version of TouchableOpacity. Gives you more control.
Image — Display an image.
ScrollView — A scrollable container for mixed content.
FlatList — An optimized list for rendering large arrays of data.
SafeAreaView — A container that respects the phone's safe areas (notch, status bar).
Modal — A popup that appears over the current screen.
ActivityIndicator — A spinning loading circle.
Switch — An on/off toggle switch.
 
Quick Reference — Most Used Tailwind Classes in React Native
Layout:
flex-1 — fill remaining space
flex-row — side by side
flex-col — stacked (default)
items-center — center on cross axis
justify-center — center on main axis
justify-between — space between children
gap-2, gap-4 — space between children
Spacing:
p-4 — padding all sides
px-4 — padding horizontal
py-4 — padding vertical
m-4 — margin all sides
mt-4, mb-4, ml-4, mr-4 — margin on one side
Colors:
bg-white, bg-gray-100, bg-blue-500 — backgrounds
text-white, text-gray-700, text-blue-600 — text color
Borders:
rounded-lg, rounded-xl, rounded-full — border radius
border, border-2 — border width
border-gray-300 — border color
Text:
text-sm, text-base, text-lg, text-xl, text-2xl — font size
font-bold, font-semibold, font-medium — font weight
text-center, text-left, text-right — alignment
Sizing:
w-full — full width
w-1/2 — half width
h-10, h-16, h-screen — height
 
Summary — What You've Learned
You started by understanding what React Native is — a tool to build mobile apps for both Android and iPhone using JavaScript.
You then saw how regular React (for websites) is different from React Native — different component names, different styling approaches, and different available elements.
You learned what Tailwind CSS is and how it replaces long CSS files with short, descriptive class names that you apply directly in your code.
You discovered NativeWind, the bridge that brings Tailwind into React Native, letting you use className just like in web development.
You built a login screen, a contacts list, a multi-screen app with navigation, and custom reusable components — all using NativeWind styling.
And you learned the most common mistakes to avoid so your code doesn't crash.
The best way to truly learn this stuff is to build things. Take the examples above and modify them. Change colors, add new fields, create new screens. Break things and fix them. That's how real developers learn. You've got everything you need right here to get started.