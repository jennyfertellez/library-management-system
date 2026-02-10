import React from 'react';
import type { BookSearchResult } from '../types/searchResults';
import { BookOpen } from 'lucide-react';

interface SearchResultCardProps {
  result: BookSearchResult;
  onSelect: (result: BookSearchResult) => void;
}

const sourceLabels = {
  openlibrary: 'OpenLibrary',
  google: 'Google Books',
  jikan: 'Jikan (Manga)'
};

const sourceBadgeColors = {
  openlibrary: 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300',
  google: 'bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-300',
  jikan: 'bg-purple-100 dark:bg-purple-900/30 text-purple-800 dark:text-purple-300'
};

const SearchResultCard: React.FC<SearchResultCardProps> = ({ result, onSelect }) => {
  return (
    <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:shadow-lg dark:hover:shadow-gray-900/50 transition-shadow cursor-pointer"
         onClick={() => onSelect(result)}>

      <div className="flex gap-4">
        {/* Thumbnail */}
        <div className="w-20 h-28 flex-shrink-0 bg-gray-100 dark:bg-gray-700 rounded overflow-hidden">
          {result.thumbnailUrl ? (
            <img src={result.thumbnailUrl} alt={result.title} className="w-full h-full object-cover" />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <BookOpen className="h-8 w-8 text-gray-400 dark:text-gray-500" />
            </div>
          )}
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
          {/* Source badge */}
          <span className={`inline-block px-2 py-1 rounded text-xs font-medium mb-2 ${sourceBadgeColors[result.source]}`}>
            {sourceLabels[result.source]}
          </span>

          <h3 className="font-semibold text-gray-900 dark:text-gray-100 line-clamp-2 mb-1">
            {result.title}
          </h3>

          {result.author && (
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
              by {result.author}
            </p>
          )}

          {result.description && (
            <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-2">
              {result.description}
            </p>
          )}

          <div className="flex gap-3 text-xs text-gray-500 dark:text-gray-400">
            {result.publishedDate && <span>📅 {result.publishedDate}</span>}
            {result.pageCount && <span>📄 {result.pageCount} pages</span>}
            {result.isbn && <span>🔢 {result.isbn}</span>}
          </div>
        </div>
      </div>

      {/* Click to add indicator */}
      <div className="mt-3 text-center">
        <span className="text-sm text-blue-600 dark:text-blue-400 font-medium">
          Click to add this book
        </span>
      </div>
    </div>
  );
};

export default SearchResultCard;