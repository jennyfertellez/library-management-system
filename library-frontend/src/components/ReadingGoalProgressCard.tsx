import React, { useState, useEffect } from 'react';
import { Calendar, Target, TrendingUp, Edit2, Trash2, CheckCircle, AlertCircle } from 'lucide-react';
import ReadingGoalApi, {
    type ReadingGoal,
    type GoalProgress
} from '../services/ReadingGoalApi';

interface GoalProgressCardProps {
  goal: ReadingGoal;
  onEdit: (goal: ReadingGoal) => void;
  onDelete: (goalId: number) => void;
}

const GoalProgressCard: React.FC<GoalProgressCardProps> = ({ goal, onEdit, onDelete }) => {
  const [progress, setProgress] = useState<GoalProgress | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchProgress();
  }, [goal.id]);

  const fetchProgress = async () => {
    try {
      setLoading(true);
      const data = await ReadingGoalApi.getGoalProgress(goal.id);
      setProgress(data);
    } catch (error) {
      console.error('Failed to fetch progress:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
    });
  };

  if (loading) {
    return (
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 animate-pulse">
        <div className="h-6 bg-gray-200 dark:bg-gray-700 rounded w-1/3 mb-4"></div>
        <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-full mb-2"></div>
        <div className="h-4 bg-gray-200 dark:bg-gray-700 rounded w-2/3"></div>
      </div>
    );
  }

  if (!progress) {
    return null;
  }

  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      {/* Header */}
      <div className="flex items-start justify-between mb-4">
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-1">
            <h3 className="text-xl font-semibold text-gray-900 dark:text-white">
              {progress.year} Reading Goal
            </h3>
            {goal.isActive && (
              <span className="px-2 py-1 text-xs font-medium bg-green-100 dark:bg-green-900 text-green-800 dark:text-green-200 rounded-full">
                Active
              </span>
            )}
          </div>
          {progress.description && (
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {progress.description}
            </p>
          )}
        </div>

        <div className="flex gap-2">
          <button
            onClick={() => onEdit(goal)}
            className="p-2 text-gray-600 dark:text-gray-400 hover:text-blue-600 dark:hover:text-blue-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
            aria-label="Edit goal"
          >
            <Edit2 className="w-4 h-4" />
          </button>
          <button
            onClick={() => onDelete(goal.id)}
            className="p-2 text-gray-600 dark:text-gray-400 hover:text-red-600 dark:hover:text-red-400 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-lg transition-colors"
            aria-label="Delete goal"
          >
            <Trash2 className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Date Range */}
      <div className="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400 mb-4">
        <Calendar className="w-4 h-4" />
        <span>
          {formatDate(progress.startDate)} - {formatDate(progress.endDate)}
        </span>
      </div>

      {/* Progress Bar */}
      <div className="mb-4">
        <div className="flex items-center justify-between mb-2">
          <span className="text-sm font-medium text-gray-700 dark:text-gray-300">
            {progress.booksRead} / {progress.targetBooks} books
          </span>
          <span className="text-sm font-semibold text-blue-600 dark:text-blue-400">
            {progress.percentageComplete}%
          </span>
        </div>
        <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-3">
          <div
            className="bg-gradient-to-r from-blue-500 to-blue-600 h-3 rounded-full transition-all duration-500"
            style={{ width: `${Math.min(progress.percentageComplete, 100)}%` }}
          ></div>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-2 gap-4 mb-4">
        {/* Books Remaining */}
        <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-3">
          <div className="flex items-center gap-2 mb-1">
            <Target className="w-4 h-4 text-gray-600 dark:text-gray-400" />
            <span className="text-xs text-gray-600 dark:text-gray-400">Remaining</span>
          </div>
          <p className="text-lg font-semibold text-gray-900 dark:text-white">
            {progress.booksRemaining} books
          </p>
        </div>

        {/* Reading Pace */}
        <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-3">
          <div className="flex items-center gap-2 mb-1">
            <TrendingUp className="w-4 h-4 text-gray-600 dark:text-gray-400" />
            <span className="text-xs text-gray-600 dark:text-gray-400">Pace</span>
          </div>
          <p className="text-lg font-semibold text-gray-900 dark:text-white">
            {progress.averageBooksPerMonth.toFixed(1)}/month
          </p>
        </div>

        {/* Days Remaining */}
        <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-3">
          <div className="flex items-center gap-2 mb-1">
            <Calendar className="w-4 h-4 text-gray-600 dark:text-gray-400" />
            <span className="text-xs text-gray-600 dark:text-gray-400">Days Left</span>
          </div>
          <p className="text-lg font-semibold text-gray-900 dark:text-white">
            {progress.daysRemaining} days
          </p>
        </div>

        {/* On Track Status */}
        <div className="bg-gray-50 dark:bg-gray-700/50 rounded-lg p-3">
          <div className="flex items-center gap-2 mb-1">
            {progress.onTrack ? (
              <CheckCircle className="w-4 h-4 text-green-600 dark:text-green-400" />
            ) : (
              <AlertCircle className="w-4 h-4 text-orange-600 dark:text-orange-400" />
            )}
            <span className="text-xs text-gray-600 dark:text-gray-400">Status</span>
          </div>
          <p className={`text-sm font-semibold ${
            progress.onTrack
              ? 'text-green-600 dark:text-green-400'
              : 'text-orange-600 dark:text-orange-400'
          }`}>
            {progress.onTrack ? 'On Track' : 'Behind Pace'}
          </p>
        </div>
      </div>

      {/* Target Pace Info */}
      <div className="text-xs text-gray-600 dark:text-gray-400 bg-blue-50 dark:bg-blue-900/20 rounded-lg p-3">
        <p className="mb-1">
          <span className="font-medium">Target pace:</span> {progress.booksPerMonth.toFixed(1)} books/month
          ({progress.booksPerWeek.toFixed(1)} books/week)
        </p>
        <p>
          <span className="font-medium">Current pace:</span> {progress.averageBooksPerMonth.toFixed(1)} books/month
        </p>
      </div>

      {/* Recently Finished Books */}
      {progress.recentlyFinished.length > 0 && (
        <div className="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
          <h4 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Recently Finished
          </h4>
          <div className="flex gap-2 overflow-x-auto">
            {progress.recentlyFinished.map((book) => (
              <div
                key={book.id}
                className="flex-shrink-0 w-16"
                title={book.title}
              >
                {book.thumbnail ? (
                  <img
                    src={book.thumbnail}
                    alt={book.title}
                    className="w-16 h-24 object-cover rounded shadow-sm"
                  />
                ) : (
                  <div className="w-16 h-24 bg-gray-200 dark:bg-gray-700 rounded flex items-center justify-center">
                    <span className="text-xs text-gray-400">No cover</span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default GoalProgressCard;