import React from 'react';
import type { BookSearchResult } from '../types/searchResults';
import { BookOpen, Calendar, FileText, Hash } from 'lucide-react';

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
    <div
      className="bg-white dark:bg-gray-800 border-2 border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:border-blue-500 dark:hover:border-blue-400 hover:shadow-lg dark:hover:shadow-gray-900/50 transition-all cursor-pointer group"
      onClick={() => onSelect(result)}
    >
      <div className="flex gap-4">
        {/* Thumbnail */}
        <div className="w-24 h-32 flex-shrink-0 bg-gray-100 dark:bg-gray-700 rounded overflow-hidden">
          {result.thumbnailUrl ? (
            <img
              src={result.thumbnailUrl}
              alt={result.title}
              className="w-full h-full object-cover"
              onError={(e) => {
                // Fallback if image fails to load
                e.currentTarget.style.display = 'none';
              }}
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center">
              <BookOpen className="h-10 w-10 text-gray-400 dark:text-gray-500" />
            </div>
          )}
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
          {/* Source badge */}
          <div className="flex items-center gap-2 mb-2">
            <span className={`inline-block px-2 py-1 rounded text-xs font-medium ${sourceBadgeColors[result.source]}`}>
              {sourceLabels[result.source]}
            </span>
            {result.source === 'jikan' && (
              <span className="text-xs text-purple-600 dark:text-purple-400 font-medium">
                📚 Manga
              </span>
            )}
          </div>

          <h3 className="font-semibold text-gray-900 dark:text-gray-100 line-clamp-2 mb-1 group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors">
            {result.title}
          </h3>

          {result.author && (
            <p className="text-sm text-gray-600 dark:text-gray-400 mb-2">
              by {result.author}
            </p>
          )}

          {result.description && (
            <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2 mb-3">
              {result.description}
            </p>
          )}

          {/* Metadata with icons */}
          <div className="flex flex-wrap gap-3 text-xs text-gray-500 dark:text-gray-400">
            {result.publishedDate && (
              <div className="flex items-center gap-1">
                <Calendar className="h-3 w-3" />
                <span>{result.publishedDate}</span>
              </div>
            )}
            {result.pageCount && (
              <div className="flex items-center gap-1">
                <FileText className="h-3 w-3" />
                <span>{result.pageCount} pages</span>
              </div>
            )}
            {result.isbn && (
              <div className="flex items-center gap-1">
                <Hash className="h-3 w-3" />
                <span className="truncate max-w-[120px]">{result.isbn}</span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Click indicator */}
      <div className="mt-3 pt-3 border-t border-gray-200 dark:border-gray-700 text-center">
        <span className="text-sm text-blue-600 dark:text-blue-400 font-medium group-hover:underline">
          Click to add this book →
        </span>
      </div>
    </div>
  );
};

export default SearchResultCard;