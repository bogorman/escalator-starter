require 'find'
require 'fileutils'

old_app = "EscalatorStarter"
new_app = "<CHANGE THIS>"

old_name = old_app.downcase
new_name = new_app.downcase

if new_app.include?("CHANGE THIS")
	raise "SET APP NAME"
end

IGNORE_FOLDERS = ['./modules/escalator/','./node_modules/','./dist']

def should_ignore?(e)
	IGNORE_FOLDERS.each do |i|
		if e.start_with?(i)
			return true
		end
	end
	false
end

Find.find('.') { |e| 
	if should_ignore?(e)
		puts "Skipping #{e}"
		next
	end

	if File.directory?(e)
		if e.end_with?(old_name)
			# puts e
			from_path = e
			to_path = e.gsub(old_name,new_name)
			puts "Moving from #{from_path} -> #{to_path}"
			FileUtils.mv from_path, to_path , :force => true
		elsif e.end_with?(old_app)
			# puts e
			from_path = e
			to_path = e.gsub(old_app,new_app)
			puts "Moving from #{from_path} -> #{to_path}"
			FileUtils.mv from_path, to_path , :force => true			
		end
	end
}

# #do files
Find.find('.') { |e| 
	if should_ignore?(e)
		puts "Skipping #{e}"
		next
	end

	if !File.directory?(e)
		#is valid file
		path = e.to_s
		if (path.end_with?(".sbt") || path.end_with?(".scala") || path.end_with?(".java") || path.end_with?(".conf") || path.end_with?(".sh") || path.end_with?(".json")) 	
			##### replace the contents
			replace1 = "'s/#{old_name}/#{new_name}/g'"
			command1 = "gsed -i #{replace1} #{path}"
			# puts command1
			`#{command1}`

			replace2 = "'s/#{old_app}/#{new_app}/g'"
			command2 = "gsed -i #{replace2} #{path}"
			# puts command2
			`#{command2}`			
		end

		filename = File.basename(path) 
		new_filename = filename.gsub(old_name,new_name).gsub(old_app,new_app)

		if (filename != new_filename)
			puts "Renaming file from #{filename} -> #{new_filename}"
			File.rename(path, path.gsub(filename,new_filename))
		end
	end
}