// ============================================================
// Module 5: Web Development Exercises
// React.js, Tailwind CSS & Next.js
//
// For each exercise:
//   Step 1 — Read the brief
//   Step 2 — Build the component
//   Step 3 — Test in your browser
// ============================================================


// -------------------------------------------------------
// Exercise 1: React — Component & Props
//
// Build a `CourseCard` component that accepts these props:
//   - title (string)
//   - instructor (string)
//   - duration (string)
//   - level (string): "Beginner" | "Intermediate" | "Advanced"
//   - price (number)
//
// Display all info clearly.
// The level badge should change colour:
//   Beginner    → green
//   Intermediate → yellow
//   Advanced    → red
//
// Render at least 3 CourseCard components in App with different data.
// -------------------------------------------------------

import { useState, useEffect } from 'react'

function CourseCard({ title, instructor, duration, level, price }) {
  // YOUR CODE HERE
}

// Level badge colours:
// Beginner    → "bg-green-100 text-green-700"
// Intermediate → "bg-yellow-100 text-yellow-700"
// Advanced    → "bg-red-100 text-red-700"


// -------------------------------------------------------
// Exercise 2: React — State & Events
//
// Build a `ShoppingCart` component:
//   - Display a list of at least 4 products (name + price)
//   - Each product has an "Add to Cart" button
//   - Show cart item count in a badge at the top
//   - Show total price of all items in cart
//   - Each cart item has a "Remove" button
//   - Cart should be empty-state friendly: "Your cart is empty"
// -------------------------------------------------------

function ShoppingCart() {
  const [cart, setCart] = useState([])

  const products = [
    { id: 1, name: "React Course", price: 49 },
    { id: 2, name: "Next.js Bootcamp", price: 79 },
    { id: 3, name: "Tailwind Masterclass", price: 29 },
    { id: 4, name: "Full Stack Bundle", price: 149 },
  ]

  const addToCart = (product) => {
    // YOUR CODE HERE
  }

  const removeFromCart = (id) => {
    // YOUR CODE HERE
  }

  const getTotal = () => {
    // YOUR CODE HERE
  }

  return (
    <div className="p-6">
      {/* YOUR JSX HERE */}
    </div>
  )
}


// -------------------------------------------------------
// Exercise 3: React — useEffect & API
//
// Build a `PostsPage` component that:
//   - Fetches posts from: https://jsonplaceholder.typicode.com/posts
//   - Shows a loading spinner/message while fetching
//   - Displays posts in a grid (title + body preview)
//   - Has a search bar that filters posts by title in real time
//   - Shows an error message if the fetch fails
//   - Limits display to first 12 posts
// -------------------------------------------------------

function PostsPage() {
  const [posts, setPosts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [search, setSearch] = useState('')

  useEffect(() => {
    // YOUR FETCH CODE HERE
  }, [])

  const filteredPosts = posts.filter(post =>
    // YOUR FILTER CODE HERE
  )

  if (loading) return <p className="text-center p-8">Loading...</p>
  if (error) return <p className="text-center text-red-500 p-8">{error}</p>

  return (
    <div className="p-6">
      {/* Search bar */}
      {/* Posts grid */}
      {/* YOUR JSX HERE */}
    </div>
  )
}


// -------------------------------------------------------
// Exercise 4: Tailwind CSS — Responsive Layout
//
// Build a fully responsive `LandingPage` component with:
//   - Navbar (logo left, links right, hamburger on mobile)
//   - Hero section (big heading, subtext, 2 CTA buttons)
//   - Features section (3 feature cards in a grid)
//     → 1 column on mobile, 3 columns on desktop
//   - Footer (copyright text centred)
//
// Use ONLY Tailwind classes — no custom CSS.
// -------------------------------------------------------

function LandingPage() {
  const [menuOpen, setMenuOpen] = useState(false)

  const features = [
    { icon: "🚀", title: "Fast", description: "Optimised for speed and performance" },
    { icon: "🔒", title: "Secure", description: "Built with security best practices" },
    { icon: "📱", title: "Responsive", description: "Works on all screen sizes" },
  ]

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navbar */}
      {/* YOUR CODE HERE */}

      {/* Hero */}
      {/* YOUR CODE HERE */}

      {/* Features */}
      {/* YOUR CODE HERE */}

      {/* Footer */}
      {/* YOUR CODE HERE */}
    </div>
  )
}


// -------------------------------------------------------
// Exercise 5: Custom Hook
//
// Build a `useLocalStorage` custom hook that:
//   - Stores and retrieves a value from localStorage
//   - Works like useState but persists across page refreshes
//   - Signature: const [value, setValue] = useLocalStorage(key, initialValue)
//
// Then build a `ThemeToggle` component that uses it to:
//   - Toggle between light and dark mode
//   - Remember the user's preference after refresh
//   - Change the page background and text colour accordingly
// -------------------------------------------------------

function useLocalStorage(key, initialValue) {
  // YOUR CODE HERE
}

function ThemeToggle() {
  const [theme, setTheme] = useLocalStorage('theme', 'light')

  return (
    <div className={`min-h-screen p-8 ${theme === 'dark' ? 'bg-gray-900 text-white' : 'bg-white text-gray-900'}`}>
      {/* YOUR CODE HERE */}
    </div>
  )
}


// -------------------------------------------------------
// Exercise 6: Next.js — Full Mini App
//
// Build a mini blog app in Next.js with these pages:
//
// 1. Home page (app/page.jsx)
//    - Fetch and display list of posts (title only) from:
//      https://jsonplaceholder.typicode.com/posts?_limit=10
//    - Each post links to its detail page
//
// 2. Post detail page (app/posts/[id]/page.jsx)
//    - Fetch single post: https://jsonplaceholder.typicode.com/posts/[id]
//    - Display title and full body
//    - Back button to return to home
//
// 3. API route (app/api/posts/route.js)
//    - GET → returns a hardcoded list of 3 custom posts
//    - POST → accepts { title, body } and returns the new post with an id
//
// Use Tailwind for all styling.
// Use Next.js Link for navigation.
// -------------------------------------------------------

// FILE: app/page.jsx
/*
export default async function Home() {
  // YOUR CODE HERE
}
*/

// FILE: app/posts/[id]/page.jsx
/*
export default async function PostPage({ params }) {
  // YOUR CODE HERE
}
*/

// FILE: app/api/posts/route.js
/*
import { NextResponse } from 'next/server'

export async function GET() {
  // YOUR CODE HERE
}

export async function POST(request) {
  // YOUR CODE HERE
}
*/


// ============================================================
// BONUS: Full Dashboard Page
//
// Build a `Dashboard` component with:
//   - Sidebar (fixed, collapsible on mobile)
//   - Top navbar with user avatar and notifications bell
//   - Stats row: 4 stat cards (users, revenue, orders, growth)
//   - Recent activity table (fetched from API or hardcoded)
//   - All responsive with Tailwind
// ============================================================

function Dashboard() {
  // YOUR CODE HERE
}


export { CourseCard, ShoppingCart, PostsPage, LandingPage, ThemeToggle, Dashboard }
