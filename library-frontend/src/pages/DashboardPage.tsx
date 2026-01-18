import React, { useEffect, useState } from 'react';
import { bookService } from '../services/bookService';
import { ReadingStats } from '../types/stats';
import StatsCard from '../components/StatsCard';
import { BookOpen, BookMarked, BookCheck, TrendingUp, Star, Users } from 'lucide-react';

const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState<ReadingStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        setLoading(true);
        const data = await bookService.getStats();
        setStats(data);
        setError(null);
      } catch (err) {
        console.error('Error fetching stats:', err);
        setError('Failed to load statistics. Make sure your backend is running.');
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4">
        <p className="text-red-800">{error}</p>
      </div>
    );
  }

  if (!stats) {
    return null;
  }

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Dashboard</h1>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <StatsCard
          title="Total Books"
          value={stats.totalBooks}
          icon={BookOpen}
          color="blue"
        />
        <StatsCard
          title="Currently Reading"
          value={stats.currentlyReading}
          icon={BookMarked}
          color="yellow"
        />
        <StatsCard
          title="Books Read"
          value={stats.booksRead}
          icon={BookCheck}
          color="green"
        />
        <StatsCard
          title="To Read"
          value={stats.booksToRead}
          icon={TrendingUp}
          color="purple"
        />
        <StatsCard
          title="Average Rating"
          value={stats.averageRating.toFixed(1)}
          icon={Star}
          color="yellow"
        />
        <StatsCard
          title="Unique Authors"
          value={stats.uniqueAuthors}
          icon={Users}
          color="blue"
        />
      </div>

      {/* This Year Section */}
      <div className="bg-white rounded-lg shadow p-6 mb-8">
        <h2 className="text-xl font-bold text-gray-900 mb-4">
          {stats.currentYear} Reading Progress
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <p className="text-sm text-gray-600">Books Read This Year</p>
            <p className="text-2xl font-bold text-gray-900">{stats.booksReadThisYear}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Books Added This Year</p>
            <p className="text-2xl font-bold text-gray-900">{stats.booksAddedThisYear}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Books Per Month</p>
            <p className="text-2xl font-bold text-gray-900">{stats.booksPerMonth.toFixed(1)}</p>
          </div>
        </div>
      </div>

      {/* Top Authors */}
      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-xl font-bold text-gray-900 mb-4">Top Authors</h2>
        <div className="space-y-3">
          {Object.entries(stats.topAuthors).map(([author, count]) => (
            <div key={author} className="flex justify-between items-center">
              <span className="text-gray-700">{author}</span>
              <span className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-medium">
                {count} {count === 1 ? 'book' : 'books'}
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;