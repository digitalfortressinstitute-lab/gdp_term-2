# Module 5: Web Development
### React.js, Tailwind CSS & Next.js

---

## What You'll Learn
- React.js fundamentals (components, props, state, hooks)
- Styling with Tailwind CSS
- Next.js (routing, SSR, SSG, API routes)
- Building and structuring real web applications
- Connecting frontend to a backend API

---

## Lesson 1: React.js Fundamentals

React is a JavaScript library for building **component-based user interfaces**.
Every piece of the UI is a component — a reusable, self-contained block.

### Setting Up
```bash
npx create-react-app my-app
cd my-app
npm start
```

---

### Components
Two types: **Function Components** (modern) and Class Components (legacy).
Always use function components.

```jsx
// Simple component
function Welcome() {
  return <h1>Welcome to DigitalFort!</h1>
}

// Arrow function style
const Welcome = () => {
  return <h1>Welcome to DigitalFort!</h1>
}

export default Welcome
```

---

### Props — Passing Data to Components
Props are how you pass data **from parent to child**.

```jsx
// Parent
function App() {
  return (
    <div>
      <UserCard name="Victor" role="Developer" />
      <UserCard name="Alice" role="Designer" />
    </div>
  )
}

// Child
function UserCard({ name, role }) {
  return (
    <div>
      <h2>{name}</h2>
      <p>{role}</p>
    </div>
  )
}
```

---

### State — Managing Data Inside a Component
State is data that **changes over time** and triggers a re-render.

```jsx
import { useState } from 'react'

function Counter() {
  const [count, setCount] = useState(0)

  return (
    <div>
      <p>Count: {count}</p>
      <button onClick={() => setCount(count + 1)}>Increment</button>
      <button onClick={() => setCount(count - 1)}>Decrement</button>
      <button onClick={() => setCount(0)}>Reset</button>
    </div>
  )
}
```

---

### useEffect — Side Effects & Data Fetching
Runs code **after** the component renders. Used for API calls, subscriptions, timers.

```jsx
import { useState, useEffect } from 'react'

function UserList() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetch('https://jsonplaceholder.typicode.com/users')
      .then(res => res.json())
      .then(data => {
        setUsers(data)
        setLoading(false)
      })
  }, []) // empty array = run once on mount

  if (loading) return <p>Loading...</p>

  return (
    <ul>
      {users.map(user => (
        <li key={user.id}>{user.name} — {user.email}</li>
      ))}
    </ul>
  )
}
```

---

### Handling Forms
```jsx
import { useState } from 'react'

function LoginForm() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (!form.email || !form.password) {
      setError('All fields are required')
      return
    }
    console.log('Submitting:', form)
  }

  return (
    <form onSubmit={handleSubmit}>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <input
        name="email"
        type="email"
        placeholder="Email"
        value={form.email}
        onChange={handleChange}
      />
      <input
        name="password"
        type="password"
        placeholder="Password"
        value={form.password}
        onChange={handleChange}
      />
      <button type="submit">Login</button>
    </form>
  )
}
```

---

### Custom Hooks
Extract reusable logic from components into custom hooks.

```jsx
// useFetch.js
import { useState, useEffect } from 'react'

function useFetch(url) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetch(url)
      .then(res => res.json())
      .then(data => {
        setData(data)
        setLoading(false)
      })
      .catch(err => {
        setError(err.message)
        setLoading(false)
      })
  }, [url])

  return { data, loading, error }
}

export default useFetch

// Usage in any component
function Posts() {
  const { data, loading, error } = useFetch('https://jsonplaceholder.typicode.com/posts')

  if (loading) return <p>Loading...</p>
  if (error) return <p>Error: {error}</p>

  return (
    <ul>
      {data.map(post => <li key={post.id}>{post.title}</li>)}
    </ul>
  )
}
```

---

## Lesson 2: Tailwind CSS

Tailwind is a **utility-first CSS framework** — style directly in your HTML/JSX using class names.
No writing custom CSS files.

### Setup with React
```bash
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init
```

---

### Core Concepts

```jsx
// Layout
<div className="flex items-center justify-between p-4">

// Spacing
<div className="m-4 p-6 mt-2 mb-8 px-4 py-2">

// Typography
<h1 className="text-3xl font-bold text-gray-800">
<p className="text-sm text-gray-500 leading-relaxed">

// Colors
<div className="bg-blue-500 text-white">
<button className="bg-green-600 hover:bg-green-700 text-white">

// Borders & Radius
<div className="border border-gray-300 rounded-lg shadow-md">

// Responsive (mobile-first)
<div className="w-full md:w-1/2 lg:w-1/3">
```

---

### Building a Card Component with Tailwind
```jsx
function ProductCard({ name, price, image, category }) {
  return (
    <div className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow">
      <img src={image} alt={name} className="w-full h-48 object-cover" />
      <div className="p-4">
        <span className="text-xs text-blue-500 uppercase font-semibold">{category}</span>
        <h3 className="text-lg font-bold text-gray-800 mt-1">{name}</h3>
        <div className="flex items-center justify-between mt-4">
          <span className="text-2xl font-bold text-gray-900">${price}</span>
          <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-sm">
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  )
}
```

---

### Responsive Navigation Bar
```jsx
function Navbar() {
  const [menuOpen, setMenuOpen] = useState(false)

  return (
    <nav className="bg-gray-900 text-white px-6 py-4">
      <div className="flex items-center justify-between">
        <span className="text-xl font-bold">DigitalFort</span>

        {/* Desktop links */}
        <ul className="hidden md:flex gap-6 text-sm">
          <li><a href="/" className="hover:text-blue-400">Home</a></li>
          <li><a href="/courses" className="hover:text-blue-400">Courses</a></li>
          <li><a href="/about" className="hover:text-blue-400">About</a></li>
        </ul>

        {/* Mobile hamburger */}
        <button className="md:hidden" onClick={() => setMenuOpen(!menuOpen)}>
          ☰
        </button>
      </div>

      {/* Mobile menu */}
      {menuOpen && (
        <ul className="flex flex-col gap-3 mt-4 md:hidden text-sm">
          <li><a href="/">Home</a></li>
          <li><a href="/courses">Courses</a></li>
          <li><a href="/about">About</a></li>
        </ul>
      )}
    </nav>
  )
}
```

---

## Lesson 3: Next.js

Next.js is a **React framework** that adds routing, server-side rendering, and API routes out of the box.

### Setup
```bash
npx create-next-app@latest my-app
cd my-app
npm run dev
```

---

### File-Based Routing
In Next.js, every file in the `app/` or `pages/` folder becomes a route automatically.

```
app/
├── page.jsx          →  /
├── about/
│   └── page.jsx      →  /about
├── courses/
│   ├── page.jsx      →  /courses
│   └── [id]/
│       └── page.jsx  →  /courses/123  (dynamic route)
```

---

### Pages & Navigation
```jsx
// app/page.jsx — Home page
import Link from 'next/link'

export default function Home() {
  return (
    <main className="p-8">
      <h1 className="text-4xl font-bold">Welcome to DigitalFort</h1>
      <Link href="/courses" className="text-blue-500 underline mt-4 block">
        Browse Courses
      </Link>
    </main>
  )
}
```

---

### Dynamic Routes
```jsx
// app/courses/[id]/page.jsx
export default function CoursePage({ params }) {
  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold">Course #{params.id}</h1>
    </div>
  )
}
```

---

### Server-Side Rendering (SSR)
Fetch data **on every request** — always fresh data.

```jsx
// app/courses/page.jsx
async function getCourses() {
  const res = await fetch('https://api.example.com/courses', {
    cache: 'no-store'  // SSR — no caching
  })
  return res.json()
}

export default async function CoursesPage() {
  const courses = await getCourses()

  return (
    <div className="p-8">
      <h1 className="text-3xl font-bold mb-6">All Courses</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {courses.map(course => (
          <div key={course.id} className="bg-white rounded-lg shadow p-4">
            <h2 className="font-bold">{course.title}</h2>
          </div>
        ))}
      </div>
    </div>
  )
}
```

---

### Static Site Generation (SSG)
Pre-render pages at **build time** — fast, cached.

```jsx
async function getCourses() {
  const res = await fetch('https://api.example.com/courses', {
    cache: 'force-cache'  // SSG — cached at build time
  })
  return res.json()
}
```

---

### API Routes
Build backend endpoints directly inside Next.js.

```jsx
// app/api/users/route.js
import { NextResponse } from 'next/server'

const users = [
  { id: 1, name: "Victor", email: "victor@mail.com" },
  { id: 2, name: "Alice", email: "alice@mail.com" },
]

export async function GET() {
  return NextResponse.json(users)
}

export async function POST(request) {
  const body = await request.json()
  const newUser = { id: users.length + 1, ...body }
  users.push(newUser)
  return NextResponse.json(newUser, { status: 201 })
}
```

---

### Next.js Project Structure (Best Practice)
```
my-app/
├── app/
│   ├── layout.jsx          # Root layout (navbar, footer)
│   ├── page.jsx            # Home page
│   ├── about/page.jsx
│   ├── courses/
│   │   ├── page.jsx
│   │   └── [id]/page.jsx
│   └── api/
│       └── courses/route.js
│
├── components/
│   ├── Navbar.jsx
│   ├── Footer.jsx
│   └── CourseCard.jsx
│
├── lib/
│   └── api.js              # API helper functions
│
└── public/                 # Static assets (images, fonts)
```

---

## Exercises
See `Web_Development_Exercises.jsx` — 6 hands-on challenges in React, Tailwind, and Next.js.

---

## Resources
- [React Docs](https://react.dev)
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [Next.js Docs](https://nextjs.org/docs)
- [Next.js Learn (official tutorial)](https://nextjs.org/learn)
