require 'find'
require 'fileutils'

OLD_PACKAGE = "com.escalatorstarter"
NEW_PACKAGE = "<CHANGE THIS>"

OLD_APP = "EscalatorStarter"
NEW_APP = "<CHANGE THIS>"

OLD_NAME = OLD_APP.downcase
NEW_NAME = NEW_APP.downcase


if NEW_APP.include?("CHANGE THIS")
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

def move_folder(from_path,to_path,package = false)
	puts "Moving from #{from_path} -> #{to_path}"

	if (!package && from_path.split("/").last != to_path.split(".").last)
		FileUtils.mv from_path, to_path , :force => true
	else
		parent_from_path = from_path.chomp("/"+from_path.split("/").last)
		parent_to_path = to_path.chomp("/"+to_path.split("/").last)

		puts "parent_from_path: #{parent_from_path}"
		puts "parent_to_path: #{parent_to_path}"

		FileUtils.mv parent_from_path, parent_to_path , :force => true
	end
end

def rename_folders(reason)
	Find.find('.') { |e| 
		if should_ignore?(e)
			puts "Skipping #{e}"
			next
		end

		if File.directory?(e)
			if e.end_with?(OLD_PACKAGE.gsub(".","/"))
				from_path = e
				to_path = e.gsub(OLD_PACKAGE.gsub(".","/"),NEW_PACKAGE.gsub(".","/"))
				puts "A Moving from #{from_path} -> #{to_path}"
				move_folder(from_path,to_path,true)				
			end

			if e.end_with?(OLD_NAME)
				# puts e
				from_path = e
				to_path = e.gsub(OLD_NAME,NEW_NAME)
				puts "B Moving from #{from_path} -> #{to_path}"
				move_folder(from_path,to_path)
			end

			if e.end_with?(OLD_APP)
				# puts e
				from_path = e
				to_path = e.gsub(OLD_APP,NEW_APP)
				puts "C Moving from #{from_path} -> #{to_path}"
				move_folder(from_path,to_path)			
			end
		end
	}
end

def rename_files(reason)
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
				replace1 = "'s/#{OLD_PACKAGE.downcase}/#{NEW_PACKAGE.downcase}/g'"
				command1 = "gsed -i #{replace1} #{path}"
				# puts command1
				`#{command1}`

				replace2 = "'s/#{OLD_PACKAGE}/#{NEW_PACKAGE}/g'"
				command2 = "gsed -i #{replace2} #{path}"
				# puts command1
				`#{command2}`

				replace3 = "'s/#{OLD_APP}/#{NEW_APP}/g'"
				command3 = "gsed -i #{replace3} #{path}"
				# puts command2
				`#{command3}`	

				replace4 = "'s/#{OLD_NAME}/#{NEW_NAME}/g'"
				command4 = "gsed -i #{replace4} #{path}"
				# puts command3
				`#{command4}`						
			end

			filename = File.basename(path) 
			new_filename = filename.gsub(OLD_NAME,NEW_NAME).gsub(OLD_APP,NEW_APP)

			if (filename != new_filename)
				puts "Renaming file from #{filename} -> #{new_filename}"
				File.rename(path, path.gsub(filename,new_filename))
			end
		end
	}
end

rename_folders("pass1")
rename_folders("pass2")

rename_files("pass1")
