import React from 'react';
import { Link, Outlet, useLocation } from 'react-router-dom';
import { BookOpen, Library, BarChart3, Settings, Target } from 'lucide-react';
import { ThemeToggle } from './ThemeToggle';

const Layout: React.FC = () => {
    const location = useLocation();

    const isActive = (path: string) => {
        if (path === '/') {
            return location.pathname === '/';
            }
        return location.pathname.startsWith(path);
        };

    const getLinkClasses = (path: string) => {
        const baseClasses = "inline-flex items-center px-1 pt-1 text-sm font-medium border-b-2";
            const activeClasses = "text-gray-900 dark:text-gray-100 border-blue-600 dark:border-blue-400";
            const inactiveClasses = "text-gray-500 dark:text-gray-400 hover:text-gray-900 dark:hover:text-gray-100 hover:border-gray-300 dark:hover:border-gray-600 border-transparent";

            return `${baseClasses} ${isActive(path) ? activeClasses : inactiveClasses}`;
        };

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
                  className={getLinkClasses('/')}
                >
                  <BarChart3 className="h-4 w-4 mr-2" />
                  Dashboard
                </Link>
                <Link
                  to="/books"
                  className={getLinkClasses('/books')}
                >
                  <Library className="h-4 w-4 mr-2" />
                  Books
                </Link>
                <Link
                  to="/shelves"
                  className={getLinkClasses('/shelves')}
                >
                  <Settings className="h-4 w-4 mr-2" />
                  Shelves
                </Link>
                <Link
                  to="/goals"
                  className={getLinkClasses('/goals')}
                >
                 <Target className="h-4 w-4 mr-2" />
                  Goals
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