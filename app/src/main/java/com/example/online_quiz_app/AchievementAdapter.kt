import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.online_quiz_app.Achievement
import com.example.online_quiz_app.R

class AchievementAdapter(private val achievements: List<Achievement>) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.achievementTitle)
        val subtitleTextView: TextView = itemView.findViewById(R.id.achievementSubtitle)
        val iconImageView: ImageView = itemView.findViewById(R.id.achievementIcon)
        val checkIconImageView: ImageView = itemView.findViewById(R.id.checkIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.achievement_description, parent, false)
        return AchievementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]

        holder.titleTextView.text = achievement.title
        holder.subtitleTextView.text = achievement.subtitle

        holder.iconImageView.setImageResource(R.drawable.icon_tt)

        // Load image from URL using Glide library
//        Glide.with(holder.itemView.context)
//            .load(achievement.iconUrl)
//            .into(holder.iconImageView)

        // Set visibility of check icon based on achievement accomplishment
        if (achievement.achieved) {
            holder.checkIconImageView.setImageResource(R.drawable.achieved)
        } else {
            holder.checkIconImageView.setImageResource(R.drawable.unachieved)
        }
    }

    override fun getItemCount(): Int {
        return achievements.size
    }
}
