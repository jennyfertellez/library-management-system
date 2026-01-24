import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import { BookOpen, Library, BarChart3, Settings } from 'lucide-react';
import { ThemeToggle } from './ThemeToggle';

const Layout: React.FC = () => {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Navigation */}
      <nav className="bg-white dark:bg-gray-800 shadow-sm border-b border-gray-200 dark:border-gray-700">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex">
              {/* Logo */}
              <Link to="/" className="flex items-center">
                <BookOpen className="h-8 w-8 text-blue-600 dark:text-blue-400" />
                <span className="ml-2 text-xl font-bold text-gray-900 dark:text-gray-100">
                  My Library
                </span>
              </Link>

              {/* Navigation Links */}
              <div className="hidden sm:ml-8 sm:flex sm:space-x-8">
                <Link
                  to="/"
                  className="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-900 dark:text-gray-100 border-b-2 border-blue-600 dark:border-blue-400"
                >
                  <BarChart3 className="h-4 w-4 mr-2" />
                  Dashboard
                </Link>
                <Link
                  to="/books"
                  className="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100 hover:border-gray-300 dark:hover:border-gray-600 border-b-2 border-transparent"
                >
                  <Library className="h-4 w-4 mr-2" />
                  Books
                </Link>
                <Link
                  to="/shelves"
                  className="inline-flex items-center px-1 pt-1 text-sm font-medium text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100 hover:border-gray-300 dark:hover:border-gray-600 border-b-2 border-transparent"
                >
                  <Settings className="h-4 w-4 mr-2" />
                  Shelves
                </Link>
              </div>
            </div>

            {/* Theme Toggle */}
            <div className="flex items-center">
              <ThemeToggle />
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 mt-auto">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <p className="text-center text-sm text-gray-500 dark:text-gray-400">
          </p>
        </div>
      </footer>
    </div>
  );
};

export default Layout;