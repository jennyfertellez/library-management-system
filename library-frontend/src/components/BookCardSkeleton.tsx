import React from 'react';

const BookCardSkeleton: React.FC = () => {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow dark:shadow-gray-900/50 p-4 animate-pulse border border-transparent dark:border-gray-700">
      {/* Book cover placeholder */}
      <div className="aspect-[2/3] bg-gray-200 dark:bg-gray-700 rounded-md mb-3"></div>

      {/* Title placeholder */}
      <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded mb-2"></div>
      <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-3/4 mb-2"></div>

      {/* Author placeholder */}
      <div className="h-3 bg-gray-200 dark:bg-gray-700 rounded w-2/3 mb-2"></div>

      {/* Status badge placeholder */}
      <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded-full w-20"></div>
    </div>
  );
};

export default BookCardSkeleton;